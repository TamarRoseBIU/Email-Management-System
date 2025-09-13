package com.example.myemailapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.myemailapp.utils.EmailActionHandler;
import com.example.myemailapp.utils.EmailListManager;
import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.example.myemailapp.R;
import com.example.myemailapp.adapters.EmailAdapter;
import com.example.myemailapp.model.Email;
import com.example.myemailapp.utils.Resource;
import com.example.myemailapp.viewmodel.SearchResultViewModel;
import com.example.myemailapp.viewmodel.LabelViewModel;
import java.util.ArrayList;
import java.util.List;
//import dagger.hilt.android.AndroidEntryPoint;

public class SearchResultFragment extends Fragment implements EmailAdapter.OnEmailListUpdateListener {
    private static final String TAG = "SearchResultFragment";

    private RecyclerView recyclerView;
    private EmailAdapter emailAdapter;
    private EmailActionHandler actionHandler;
    private String authToken;
    private List<Email> emailList = new ArrayList<>();
    //    private SwipeRefreshLayout swipeRefreshLayout;
//    private LinearProgressIndicator progressIndicator;
    private MaterialToolbar toolbar;
    private Button buttonBack;
    private SearchResultViewModel viewModel;
    private LabelViewModel labelViewModel;
    private List<String> availableLabels = new ArrayList<>();
    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private final long REFRESH_INTERVAL = 3000; // 3 seconds
    private TextView emptyText;

    // Store the original search parameters to detect changes
    private String originalSearchTerm;
    private String originalLabelId;

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            viewModel.refreshEmails();
            refreshHandler.postDelayed(this, REFRESH_INTERVAL);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModels
        viewModel = new ViewModelProvider(this).get(SearchResultViewModel.class);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);

        SharedPreferences prefs = requireContext().getSharedPreferences("auth", MODE_PRIVATE);
        authToken = prefs.getString("jwt", "");
        actionHandler = new EmailActionHandler(requireContext(), authToken);

        // Get arguments and set search parameters
        Bundle args = getArguments();
        if (args != null) {
            String searchType = args.getString("search_type", "label");
            String searchTerm = args.getString("search_term", "");
            String labelId = args.getString("label_id", "");

            // Store original parameters for comparison
            originalSearchTerm = searchTerm;
            originalLabelId = labelId;

            viewModel.setSearchParameters(searchType, searchTerm, labelId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        buttonBack = view.findViewById(R.id.button_back);
        initViews(view);
        setupRecyclerView();
        setupObservers();
        observeViewModel();
        setupLabelObservers();

        // Load initial labels
        labelViewModel.loadLabels();

        // in search by query hide it
        String searchType = getArguments() != null ? getArguments().getString("search_type") : null;
        if ("query".equals(searchType) && buttonBack != null) {
            buttonBack.setVisibility(View.GONE);
        }


        return view;
    }

    private void setupObservers() {
        viewModel.getShouldShowEmpty().observe(getViewLifecycleOwner(), shouldShowEmpty -> {
            if (shouldShowEmpty) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onViewCreated(@org.jspecify.annotations.NonNull View view, @org.jspecify.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
//        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
//        progressIndicator = view.findViewById(R.id.progress_indicator);
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyText = view.findViewById(R.id.no_searched_emails_text);
        // Set up back button
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
//        toolbar.setNavigationOnClickListener(v -> {
//            if (getActivity() != null) {
//                getActivity().onBackPressed();
//            }
//        });
    }

    private void setupRecyclerView() {
        emailAdapter = new EmailAdapter(emailList, new EmailAdapter.OnEmailClickListener() {
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
                // Search result emails typically don't have a restore action
            }

            @Override
            public void onDeleteClick(Email email) {
                // Use universal action handler for delete
                actionHandler.deleteEmail(email.getId(), new EmailActionHandler.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        onEmailRemoved(email.getId());
                        Toast.makeText(getContext(), "Email deleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Delete failed: " + error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Marked as read", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Mark as read failed: " + error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Marked as unread", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Mark as unread failed: " + error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Marked as spam", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Mark as spam failed: " + error, Toast.LENGTH_SHORT).show();
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

    private void setupLabelObservers() {
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
                            Toast.makeText(getContext(), "Labels updated successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(getContext(), "Labels update failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observeViewModel() {
        // Observe search term for toolbar title and label change detection
        viewModel.searchTerm.observe(getViewLifecycleOwner(), searchTerm -> {
            if (viewModel.searchType.getValue() != null &&
                    !"draft".equals(viewModel.searchType.getValue())) {
                if ("query".equals(viewModel.searchType.getValue())) {
                    toolbar.setTitle(getString(R.string.search_type_query_title, searchTerm));
                } else {
                    toolbar.setTitle(getString(R.string.search_type_label_title, searchTerm));
                }

            }
        });

        // Observe label existence
        viewModel.labelExists.observe(getViewLifecycleOwner(), exists -> {
            if (exists != null && !exists) {
                // Label no longer exists, navigate back
                navigateBack("Label has been deleted or renamed");
            }
        });

        // Observe email list changes
        viewModel.emails.observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        showLoading(true);
                        break;

                    case SUCCESS:
                        showLoading(false);
                        updateEmailList(resource.data);

                        // Check if label has changed after successful load
                        if (viewModel.hasLabelChanged()) {
                            navigateBack("Label has been renamed");
                        }
                        break;

                    case ERROR:
                        showLoading(false);
                        showError(resource.message);

                        // Check if error indicates label doesn't exist
                        if (resource.message != null &&
                                (resource.message.toLowerCase().contains("label not found") ||
                                        resource.message.toLowerCase().contains("label does not exist") ||
                                        resource.message.toLowerCase().contains("not found"))) {
                            navigateBack("Label no longer exists");
                        }
                        break;
                }
            }
        });
    }

    /**
     * Navigate back to the previous page with optional message
     */
    private void navigateBack(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }

        if (getActivity() != null) {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void updateEmailList(List<Email> emails) {
        emailList.clear();
        if (emails != null) {
            emailList.addAll(emails);
        }
        emailAdapter.notifyDataSetChanged();

        // Show empty state if no emails found
        if (emailList.isEmpty()) {
            showEmptyState();
        }
    }

    private void showLoading(boolean show) {
//        if (show) {
//            progressIndicator.setVisibility(View.VISIBLE);
//            progressIndicator.show();
//        } else {
//            progressIndicator.setVisibility(View.GONE);
//            progressIndicator.hide();
////            swipeRefreshLayout.setRefreshing(false);
//        }
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message != null ? message : "An error occurred",
                Toast.LENGTH_LONG).show();
    }

    private void showEmptyState() {
        updateEmptyText();
//        String searchType = viewModel.searchType.getValue();
//        String searchTerm = viewModel.searchTerm.getValue();
//
//        String message = "draft".equals(searchType) ?
//                "No drafts found" :
//                "No emails found for label: " + searchTerm;
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh emails when returning to fragment
        // This handles the case where labels might have been edited
        viewModel.refreshEmails();
        labelViewModel.loadLabels();
        // Start periodic refresh
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
        labelViewModel.startPeriodicRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop periodic refresh when fragment is paused
        refreshHandler.removeCallbacks(refreshRunnable);
        labelViewModel.stopPeriodicRefresh();
    }

    @Override
    public void onEmailRemoved(String emailId) {
        // Remove email from list and update adapter
        emailList = EmailListManager.removeEmailFromList(emailList, emailId);
        emailAdapter.updateEmailList(emailList);

        // Update empty state
        if (emailList.isEmpty()) {
            showEmptyState();
        }

        Log.d(TAG, "Email removed: " + emailId);
    }

    @Override
    public void onEmailReadStatusChanged(String emailId, boolean isRead) {
        // Update email read status in list
        emailList = EmailListManager.updateEmailReadStatus(emailList, emailId, isRead);
        emailAdapter.updateEmailList(emailList);

        Log.d(TAG, "Email read status changed: " + emailId + " -> " + isRead);
    }

    @Override
    public void onEmailStarStatusChanged(String emailId, boolean isStarred) {
        // Update email star status in list
        emailList = EmailListManager.updateEmailStarStatus(emailList, emailId, isStarred);
        emailAdapter.updateEmailList(emailList);

        Log.d(TAG, "Email star status changed: " + emailId + " -> " + isStarred);
    }

    @Override
    public void onEmailLabelsChanged(String emailId, List<String> labels) {
        // Update email labels in list
        emailList = EmailListManager.updateEmailLabels(emailList, emailId, labels);
        emailAdapter.updateEmailList(emailList);

        // Notify specific item change for better performance
        int pos = emailAdapter.getPositionById(emailId);
        if (pos != -1) {
            emailAdapter.notifyItemChanged(pos);
        }

        Log.d(TAG, "Email labels changed: " + emailId + " -> " + labels);
    }
    @SuppressLint("StringFormatInvalid")
    private void updateEmptyText() {
        String searchType = viewModel.searchType.getValue();
        String searchTerm = viewModel.searchTerm.getValue();

        if ("query".equals(searchType)) {
            emptyText.setText(getString(R.string.no_emails_with_query, searchTerm));
        } else {
            emptyText.setText(getString(R.string.no_emails_with_label, searchTerm));
        }
        emptyText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }


}