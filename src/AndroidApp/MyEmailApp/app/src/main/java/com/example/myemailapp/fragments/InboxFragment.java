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
import android.widget.ToggleButton;

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
import com.example.myemailapp.viewmodel.InboxViewModel;
import com.example.myemailapp.viewmodel.LabelViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static android.content.Context.MODE_PRIVATE;

public class InboxFragment extends Fragment implements EmailAdapter.OnEmailListUpdateListener {
    private static final String TAG = "InboxFragment";
    private static final long REFRESH_INTERVAL = 2000; // 2 seconds

    private RecyclerView recyclerView;
    private EmailAdapter emailAdapter;
    private TextView emptyText;
    private ToggleButton toggleUnreadOnly;
    //    private TextView titleText;
    private Handler handler;
    private Runnable refreshRunnable;

    private InboxViewModel inboxViewModel;
    private LabelViewModel labelViewModel;
    private List<Email> inboxEmails = new ArrayList<>();
    private List<Email> allEmails = new ArrayList<>(); // Store all emails
    private List<String> availableLabels = new ArrayList<>();
    private EmailActionHandler actionHandler;
    private String authToken;
    private boolean showUnreadOnly = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get auth token
        SharedPreferences prefs = requireContext().getSharedPreferences("auth", MODE_PRIVATE);
        authToken = prefs.getString("jwt", "");

        // Initialize universal action handler
        actionHandler = new EmailActionHandler(requireContext(), authToken);

        // Initialize ViewModels
        inboxViewModel = new ViewModelProvider(this).get(InboxViewModel.class);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupObservers();
        setupToggleButton();

        // Load initial data
        inboxViewModel.loadInboxEmails();
        labelViewModel.loadLabels();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_inbox);
        emptyText = view.findViewById(R.id.empty_inbox_text);
        toggleUnreadOnly = view.findViewById(R.id.toggle_unread_only);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupToggleButton() {
//        toggleUnreadOnly.setTextOn("Unread Only");
//        toggleUnreadOnly.setTextOff("All Emails");
        toggleUnreadOnly.setChecked(showUnreadOnly);

        toggleUnreadOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showUnreadOnly = isChecked;
            applyEmailFilter();
        });
    }

    private void applyEmailFilter() {
        List<Email> filteredEmails;

        if (showUnreadOnly) {
            // Filter to show only unread emails
            filteredEmails = allEmails.stream()
                    .filter(email -> !email.isRead())
                    .collect(Collectors.toList());
        } else {
            // Show all emails
            filteredEmails = new ArrayList<>(allEmails);
        }

        inboxEmails.clear();
        inboxEmails.addAll(filteredEmails);
        emailAdapter.notifyDataSetChanged();

        // Update empty state based on filtered results
        inboxViewModel.updateEmptyState(inboxEmails);

        Log.d(TAG, "Applied filter - Show unread only: " + showUnreadOnly +
                ", Filtered emails: " + filteredEmails.size() +
                ", Total emails: " + allEmails.size());
    }

    private void setupRecyclerView() {
        emailAdapter = new EmailAdapter(inboxEmails, new EmailAdapter.OnEmailClickListener() {
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
                // Inbox emails typically don't have a restore action
            }

            @Override
            public void onDeleteClick(Email email) {
                // Use universal action handler for delete
                actionHandler.deleteEmail(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        onEmailRemoved(email.getId());
//                        actionHandler.showToast("Email deleted");
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
//                        actionHandler.showToast("Mark as unread failed: " + error);
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
        // Observe inbox emails
        inboxViewModel.getInboxEmails().observe(getViewLifecycleOwner(), emails -> {
            if (emails != null) {
                // Store all emails
                allEmails.clear();
                allEmails.addAll(emails);

                // Apply current filter
                applyEmailFilter();

                Log.d(TAG, "Updated inbox emails: " + emails.size() + " total items");
            }
        });

        // Observe empty state
        inboxViewModel.getShouldShowEmpty().observe(getViewLifecycleOwner(), shouldShowEmpty -> {
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
        inboxViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Inbox Error: " + error);
            }
        });

        // Observe loading state
        inboxViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Inbox Loading state: " + isLoading);
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
        // Remove from both lists
        allEmails = EmailListManager.removeEmailFromList(allEmails, emailId);
        inboxEmails = EmailListManager.removeEmailFromList(inboxEmails, emailId);
        emailAdapter.updateEmailList(inboxEmails);
        inboxViewModel.updateEmptyState(inboxEmails);
    }

    @Override
    public void onEmailReadStatusChanged(String emailId, boolean isRead) {
        // Update both lists
        allEmails = EmailListManager.updateEmailReadStatus(allEmails, emailId, isRead);
        inboxEmails = EmailListManager.updateEmailReadStatus(inboxEmails, emailId, isRead);

        // If showing unread only and email was marked as read, remove it from view
        if (showUnreadOnly && isRead) {
            applyEmailFilter();
        } else {
            emailAdapter.updateEmailList(inboxEmails);
        }
    }

    @Override
    public void onEmailStarStatusChanged(String emailId, boolean isStarred) {
        // Update both lists
        allEmails = EmailListManager.updateEmailStarStatus(allEmails, emailId, isStarred);
        inboxEmails = EmailListManager.updateEmailStarStatus(inboxEmails, emailId, isStarred);
        emailAdapter.updateEmailList(inboxEmails);
    }

    @Override
    public void onEmailLabelsChanged(String emailId, List<String> labels) {
        // Update both lists
        allEmails = EmailListManager.updateEmailLabels(allEmails, emailId, labels);
        inboxEmails = EmailListManager.updateEmailLabels(inboxEmails, emailId, labels);
        emailAdapter.updateEmailList(inboxEmails);
//        emailAdapter.notifyDataSetChanged();
        int pos = emailAdapter.getPositionById(emailId);
        if (pos != -1) emailAdapter.notifyItemChanged(pos);
    }

    @Override
    public void onResume() {
        super.onResume();
        inboxViewModel.loadInboxEmails();
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
                inboxViewModel.loadInboxEmails();
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