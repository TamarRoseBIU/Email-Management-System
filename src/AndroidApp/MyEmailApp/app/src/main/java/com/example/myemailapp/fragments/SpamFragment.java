package com.example.myemailapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myemailapp.R;
import com.example.myemailapp.adapters.EmailAdapter;
import com.example.myemailapp.model.Email;
import com.example.myemailapp.utils.EmailActionHandler;
import com.example.myemailapp.utils.EmailListManager;
import com.example.myemailapp.viewmodel.SpamViewModel;
import com.example.myemailapp.viewmodel.LabelViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SpamFragment extends Fragment implements EmailAdapter.OnEmailListUpdateListener {
    private static final String TAG = "SpamFragment";
    private static final long REFRESH_INTERVAL = 2000;

    private RecyclerView recyclerView;
    private EmailAdapter emailAdapter;
    private TextView emptyText;
    private Handler handler;
    private Runnable refreshRunnable;

    private SpamViewModel spamViewModel;
    private LabelViewModel labelViewModel;
    private List<Email> spamEmails = new ArrayList<>();
    private List<String> availableLabels = new ArrayList<>();
    private EmailActionHandler actionHandler;
    private String authToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = requireContext().getSharedPreferences("auth", MODE_PRIVATE);
        authToken = prefs.getString("jwt", "");

        actionHandler = new EmailActionHandler(requireContext(), authToken);
        spamViewModel = new ViewModelProvider(this).get(SpamViewModel.class);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spam, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_spam);
        emptyText = view.findViewById(R.id.empty_spam_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emailAdapter = new EmailAdapter(spamEmails, new EmailAdapter.OnEmailClickListener() {
            @Override
            public void onEmailClick(Email email) {
                // Navigate to detail
                onMarkReadClick(email);
                Bundle args = new Bundle();
                args.putString("from",      email.getFrom());
                args.putString("subject",   email.getSubject());
                args.putString("timestamp", email.getTimeStamp());
                args.putString("body",      email.getBody());
                // Labels: convert list → comma‐separated string (or empty)
                args.putString("labels",
                        email.getLabels() != null
                                ? TextUtils.join(", ", email.getLabels())
                                : "");

                EmailDetailFragment detailFragment = new EmailDetailFragment();
                detailFragment.setArguments(args);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onRestoreClick(Email email) {
                spamViewModel.restoreFromSpam(email);
            }

            @Override
            public void onDeleteClick(Email email) {
                actionHandler.deleteEmail(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        onEmailRemoved(email.getId());
                    }

                    @Override
                    public void onError(String error) {
                    }
                });
            }

            @Override
            public void onMarkReadClick(Email email) {
                actionHandler.markAsRead(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        onEmailReadStatusChanged(email.getId(), true);
                    }

                    @Override
                    public void onError(String error) {
                    }
                });
            }

            @Override
            public void onMarkUnreadClick(Email email) {
                actionHandler.markAsUnread(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        onEmailReadStatusChanged(email.getId(), false);
                    }

                    @Override
                    public void onError(String error) {
                    }
                });
            }

            @Override
            public void onSpamClick(Email email) {
                spamViewModel.restoreFromSpam(email);
            }

            @Override
            public void onLabelsClick(Email email) {
                showEditLabelsDialog(email);
            }
        }, false, actionHandler, this);

        recyclerView.setAdapter(emailAdapter);

        setupObservers();

        spamViewModel.loadSpamEmails();
        labelViewModel.loadLabels();

        return view;
    }

    private void setupObservers() {
        spamViewModel.getSpamEmails().observe(getViewLifecycleOwner(), emails -> {
            spamEmails.clear();
            spamEmails.addAll(emails);
            emailAdapter.notifyDataSetChanged();
            spamViewModel.updateEmptyState(emails);
        });

        spamViewModel.getShouldShowEmpty().observe(getViewLifecycleOwner(), shouldShowEmpty -> {
            emptyText.setVisibility(shouldShowEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(shouldShowEmpty ? View.GONE : View.VISIBLE);
        });

        spamViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        spamViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading: " + isLoading);
        });

        labelViewModel.getLabels().observe(getViewLifecycleOwner(), labels -> {
            availableLabels.clear();
            availableLabels.addAll(labels);
        });
    }

    private void showEditLabelsDialog(Email email) {
        if (availableLabels.isEmpty()) {
            Toast.makeText(getContext(), "No labels yet...", Toast.LENGTH_SHORT).show();
            labelViewModel.refreshLabels();
            return;
        }

        String[] allLabels = availableLabels.toArray(new String[0]);
        boolean[] checked = new boolean[allLabels.length];
        List<String> currentLabels = email.getLabels();

        for (int i = 0; i < allLabels.length; i++) {
            checked[i] = currentLabels != null && currentLabels.contains(allLabels[i]);
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Labels")
                .setMultiChoiceItems(allLabels, checked, (dialog, which, isChecked) -> {
                    checked[which] = isChecked;
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    List<String> newLabels = new ArrayList<>();
                    for (int i = 0; i < allLabels.length; i++) {
                        if (checked[i]) {
                            newLabels.add(allLabels[i]);
                        }
                    }
                    actionHandler.saveLabels(email.getId(), newLabels, new EmailActionHandler.ActionCallback() {
                        @Override
                        public void onSuccess() {
                            onEmailLabelsChanged(email.getId(), newLabels);
                        }

                        @Override
                        public void onError(String error) {
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onEmailRemoved(String emailId) {
        spamEmails = EmailListManager.removeEmailFromList(spamEmails, emailId);
        emailAdapter.updateEmailList(spamEmails);
        spamViewModel.updateEmptyState(spamEmails);
    }

    @Override
    public void onEmailReadStatusChanged(String emailId, boolean isRead) {
        spamEmails = EmailListManager.updateEmailReadStatus(spamEmails, emailId, isRead);
        emailAdapter.updateEmailList(spamEmails);
    }

    @Override
    public void onEmailStarStatusChanged(String emailId, boolean isStarred) {
        spamEmails = EmailListManager.updateEmailStarStatus(spamEmails, emailId, isStarred);
        emailAdapter.updateEmailList(spamEmails);
    }

    @Override
    public void onEmailLabelsChanged(String emailId, List<String> labels) {
        spamEmails = EmailListManager.updateEmailLabels(spamEmails, emailId, labels);
        emailAdapter.updateEmailList(spamEmails);
    }

    @Override
    public void onResume() {
        super.onResume();
        spamViewModel.loadSpamEmails();
        labelViewModel.loadLabels();
        startPeriodicRefresh();
        labelViewModel.startPeriodicRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPeriodicRefresh();
        labelViewModel.stopPeriodicRefresh();
    }

    private void startPeriodicRefresh() {
        handler = new Handler();
        refreshRunnable = () -> {
            spamViewModel.loadSpamEmails();
            handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
        };
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    private void stopPeriodicRefresh() {
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }
}
