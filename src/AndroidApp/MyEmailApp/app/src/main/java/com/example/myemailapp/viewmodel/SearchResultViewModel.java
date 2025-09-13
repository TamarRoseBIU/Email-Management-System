package com.example.myemailapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.example.myemailapp.model.Email;
import com.example.myemailapp.repository.InboxRepository;
import com.example.myemailapp.utils.Resource;
import java.util.List;

public class SearchResultViewModel extends AndroidViewModel {
    private static final String TAG = "SearchResultViewModel";

    private final InboxRepository emailRepository;

    private final MutableLiveData<Resource<List<Email>>> _emails = new MutableLiveData<>();
    public final LiveData<Resource<List<Email>>> emails = _emails;

    private final MutableLiveData<String> _searchType = new MutableLiveData<>();
    public final LiveData<String> searchType = _searchType;

    private final MutableLiveData<String> _searchTerm = new MutableLiveData<>();
    public final LiveData<String> searchTerm = _searchTerm;

    private final MutableLiveData<Boolean> _labelExists = new MutableLiveData<>(true);
    public final LiveData<Boolean> labelExists = _labelExists;

    private final MutableLiveData<Boolean> _shouldShowEmpty = new MutableLiveData<>();
    public final LiveData<Boolean> shouldShowEmpty = _shouldShowEmpty;

    private Observer<List<Email>> searchResultsObserver;
    private String originalSearchTerm;
    private String originalLabelId;

    public SearchResultViewModel(@NonNull Application application) {
        super(application);
        this.emailRepository = new InboxRepository(application.getApplicationContext());
        _shouldShowEmpty.setValue(false);

        // Observe search results from repository
        searchResultsObserver = new Observer<List<Email>>() {
            @Override
            public void onChanged(List<Email> emails) {
                _emails.setValue(Resource.success(emails));
                // Check if label still exists based on search results
                checkLabelValidity(emails);
                // Update empty state
                updateEmptyState(emails);
            }
        };
        emailRepository.getSearchResults().observeForever(searchResultsObserver);

        // Also observe loading state
        emailRepository.getIsLoading().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading != null && isLoading) {
                    _emails.setValue(Resource.loading(null));
                }
            }
        });

        // Observe error messages
        emailRepository.getErrorMessage().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    _emails.setValue(Resource.error(error, null));
                    // If error indicates label doesn't exist, update label existence
                    if (error.toLowerCase().contains("label not found") ||
                            error.toLowerCase().contains("label does not exist")) {
                        _labelExists.setValue(false);
                    }
                }
            }
        });
    }
    public void setSearchParameters(String searchType, String searchTerm, String labelId) {
        _searchType.setValue(searchType);
        _searchTerm.setValue(searchTerm);

        // Store original parameters for comparison
        this.originalSearchTerm = searchTerm;
        this.originalLabelId = labelId;

        loadEmails(searchType, searchTerm);
    }

    public void loadEmails(String searchType, String searchTerm) {
        _emails.setValue(Resource.loading(null));
        if ("label".equals(searchType)) {
            emailRepository.searchEmailsByLabel(searchTerm);
        } else if ("query".equals(searchType)) {
            emailRepository.searchEmailsByQuery(searchTerm);
        } else {
        }
    }

    public void refreshEmails() {
        String currentSearchType = _searchType.getValue();
        String currentSearchTerm = _searchTerm.getValue();
        if (currentSearchType != null && currentSearchTerm != null) {
            loadEmails(currentSearchType, currentSearchTerm);
        }
    }

    // Email action methods similar to InboxViewModel
    public void deleteFromSearch(Email email) {
        emailRepository.deleteFromInbox(email);
        Log.d(TAG, "Delete email from search: " + email.getId());
    }

    public void markAsRead(Email email) {
        emailRepository.markAsRead(email);
        Log.d(TAG, "Mark email as read: " + email.getId());
    }

    public void markAsUnread(Email email) {
        emailRepository.markAsUnread(email);
        Log.d(TAG, "Mark email as unread: " + email.getId());
    }

    public void markAsSpam(Email email) {
        emailRepository.markAsSpam(email);
        Log.d(TAG, "Mark email as spam: " + email.getId());
    }

    public void editLabels(Email email, List<String> newLabels) {
        emailRepository.editLabels(email, newLabels);
        Log.d(TAG, "Edit labels for email: " + email.getId() + " -> " + newLabels);
    }

    public void updateEmptyState(List<Email> emails) {
        _shouldShowEmpty.setValue(emails == null || emails.isEmpty());

    }

    public LiveData<Boolean> getShouldShowEmpty() {
        return _shouldShowEmpty;
    }

    public Email getEmailById(String emailId) {
        if (emailId == null) {
            Log.w(TAG, "getEmailById called with null emailId");
            return null;
        }

        Log.d(TAG, "Searching email with ID: " + emailId);
        Resource<List<Email>> currentResource = _emails.getValue();

        if (currentResource != null && currentResource.data != null) {
            for (Email email : currentResource.data) {
                Log.d(TAG, "email with ID: " + email.getId());
                if (emailId.equals(email.getId())) {
                    Log.d(TAG, "Found email with ID: " + emailId);
                    return email;
                }
            }
        }

        Log.w(TAG, "Email not found with ID: " + emailId);
        return null;
    }

    /**
     * Check if the label still exists by examining search results
     * This can be enhanced based on your specific API response structure
     */
    private void checkLabelValidity(List<Email> emails) {
        String currentSearchTerm = _searchTerm.getValue();

        if (originalSearchTerm != null && currentSearchTerm != null) {
            // If the search term has changed from original, label might have been renamed
            if (!originalSearchTerm.equals(currentSearchTerm)) {
                _labelExists.setValue(false);
                return;
            }
        }

        // Additional checks can be added here based on your API response
        // For example, if the API returns a specific indicator for label existence
        // or if empty results with specific error codes indicate label deletion

        _labelExists.setValue(true);
    }

    /**
     * Check if the current label name matches the original
     */
    public boolean hasLabelChanged() {
        String currentSearchTerm = _searchTerm.getValue();
        return originalSearchTerm != null && currentSearchTerm != null &&
                !originalSearchTerm.equals(currentSearchTerm);
    }

    /**
     * Get the original search term for comparison
     */
    public String getOriginalSearchTerm() {
        return originalSearchTerm;
    }

    /**
     * Get the original label ID for comparison
     */
    public String getOriginalLabelId() {
        return originalLabelId;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up observers to prevent memory leaks
        if (searchResultsObserver != null) {
            emailRepository.getSearchResults().removeObserver(searchResultsObserver);
        }
    }

}