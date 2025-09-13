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
import com.example.myemailapp.viewmodel.StarredViewModel;
import com.example.myemailapp.viewmodel.LabelViewModel;


import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class StarredFragment extends Fragment implements EmailAdapter.OnEmailListUpdateListener {
    private static final String TAG = "StarredFragment";
    private static final long REFRESH_INTERVAL = 2000; // 2 seconds

    private RecyclerView recyclerView;
    private EmailAdapter emailAdapter;
    private TextView emptyText;
    //    private TextView titleText;
    private Handler handler;
    private Runnable refreshRunnable;

    private StarredViewModel starredViewModel;
    private LabelViewModel labelViewModel;
    private List<Email> starredEmails = new ArrayList<>();
    private List<String> availableLabels = new ArrayList<>();
    private EmailActionHandler actionHandler;
    private String authToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get auth token
        SharedPreferences prefs = requireContext().getSharedPreferences("auth", MODE_PRIVATE);
        authToken = prefs.getString("jwt", "");

        // Initialize universal action handler
        actionHandler = new EmailActionHandler(requireContext(), authToken);

        // Initialize ViewModels
        starredViewModel = new ViewModelProvider(this).get(StarredViewModel.class);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starred, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupObservers();

        // Load initial data
        starredViewModel.loadStarredEmails();
        labelViewModel.loadLabels();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_starred);
        emptyText = view.findViewById(R.id.empty_starred_text);
//        titleText = view.findViewById(R.id.sent_title);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupRecyclerView() {
        emailAdapter = new EmailAdapter(starredEmails, new EmailAdapter.OnEmailClickListener() {
            @Override
            public void onEmailClick(Email email) {
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
                // Sent emails typically don't have a restore action
            }

            @Override
            public void onDeleteClick(Email email) {
                // Use universal action handler for delete
                actionHandler.deleteEmail(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        onEmailRemoved(email.getId());
//                        actionHandler.showToast("Email deleted permanently");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Delete failed: " + error);
                    }
                });
            }

            @Override
            public void onMarkReadClick(Email email) {
                // Use universal action handler for mark as read
                actionHandler.markAsRead(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        onEmailReadStatusChanged(email.getId(), true);
//                        actionHandler.showToast("Marked as read");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Mark as read failed: " + error);
                    }
                });
            }

            @Override
            public void onMarkUnreadClick(Email email) {
                // Use universal action handler for mark as unread
                actionHandler.markAsUnread(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        onEmailReadStatusChanged(email.getId(), false);
//                        actionHandler.showToast("Marked as unread");
                    }

                    @Override
                    public void onError(String error) {
//

                    }
                });
            }

            @Override
            public void onSpamClick(Email email) {
                // Use universal action handler for spam
                actionHandler.markAsSpam(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        onEmailRemoved(email.getId());
//                        actionHandler.showToast("Marked as spam");
                    }

                    @Override
                    public void onError(String error) {
//                        actionHandler.showToast("Mark as spam failed: " + error);
                    }
                });
            }

            @Override
            public void onLabelsClick(Email email) {
                showEditLabelsDialog(email);
            }
        }, false, actionHandler, this); // Pass false for isTrash, and pass action handler and listener

        recyclerView.setAdapter(emailAdapter);
    }

    private void setupObservers() {
        // Observe sent emails
        starredViewModel.getStarredEmails().observe(getViewLifecycleOwner(), emails -> {
            if (emails != null) {
                starredEmails.clear();
                starredEmails.addAll(emails);
                emailAdapter.notifyDataSetChanged();

                // Update empty state
                starredViewModel.updateEmptyState(emails);

                Log.d(TAG, "Updated starred emails: " + emails.size() + " items");
            }
        });

        // Observe empty state
        starredViewModel.getShouldShowEmpty().observe(getViewLifecycleOwner(), shouldShowEmpty -> {
            if (shouldShowEmpty) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
//                titleText.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
//                titleText.setVisibility(View.VISIBLE);
            }
        });

        // Observe error messages
        starredViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Starred Error: " + error);
            }
        });

        // Observe loading state
        starredViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Starred Loading state: " + isLoading);
        });

        // Observe labels from LabelViewModel
        labelViewModel.getLabels().observe(getViewLifecycleOwner(), labels -> {
            if (labels != null) {
                availableLabels.clear();
                availableLabels.addAll(labels);
                Log.d(TAG, "Available labels updated: " + labels.size() + " labels");
            }
        });

        // Observe label errors
        labelViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), "Labels: " + error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Label Error: " + error);
            }
        });

        // Observe label loading state
        labelViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Labels Loading state: " + isLoading);
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
//                            actionHandler.showToast("Labels updated successfully");
                        }

                        @Override
                        public void onError(String error) {
//                            actionHandler.showToast("Labels update failed: " + error);
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onEmailRemoved(String emailId) {
        starredEmails = EmailListManager.removeEmailFromList(starredEmails, emailId);
        emailAdapter.updateEmailList(starredEmails);
        starredViewModel.updateEmptyState(starredEmails);
    }

    @Override
    public void onEmailReadStatusChanged(String emailId, boolean isRead) {
        starredEmails = EmailListManager.updateEmailReadStatus(starredEmails, emailId, isRead);
        emailAdapter.updateEmailList(starredEmails);
    }

    @Override
    public void onEmailStarStatusChanged(String emailId, boolean isStarred) {
        starredEmails = EmailListManager.updateEmailStarStatus(starredEmails, emailId, isStarred);
        emailAdapter.updateEmailList(starredEmails);
    }

    @Override
    public void onEmailLabelsChanged(String emailId, List<String> labels) {
        starredEmails = EmailListManager.updateEmailLabels(starredEmails, emailId, labels);
        emailAdapter.updateEmailList(starredEmails);
    }

    @Override
    public void onResume() {
        super.onResume();
        starredViewModel.loadStarredEmails();
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
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                starredViewModel.loadStarredEmails();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    private void stopPeriodicRefresh() {
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }
}