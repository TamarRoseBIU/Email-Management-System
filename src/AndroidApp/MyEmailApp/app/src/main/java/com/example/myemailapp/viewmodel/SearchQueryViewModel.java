package com.example.myemailapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.myemailapp.model.Email;
import com.example.myemailapp.repository.InboxRepository;
import com.example.myemailapp.repository.SearchRepository;
import com.example.myemailapp.utils.Resource;

import java.util.List;

public class SearchQueryViewModel extends AndroidViewModel {
    private static final String TAG = "SearchQueryViewModel";

    private final SearchRepository emailRepository;

    private final MutableLiveData<Resource<List<Email>>> _emails = new MutableLiveData<>();
    public final LiveData<Resource<List<Email>>> emails = _emails;

    private final MutableLiveData<String> _searchTerm = new MutableLiveData<>();
    public final LiveData<String> searchTerm = _searchTerm;

    private final MutableLiveData<Boolean> _shouldShowEmpty = new MutableLiveData<>(false);
    public final LiveData<Boolean> shouldShowEmpty = _shouldShowEmpty;

    private Observer<List<Email>> searchResultsObserver;

    public SearchQueryViewModel(@NonNull Application application) {
        super(application);
        this.emailRepository = new SearchRepository(application.getApplicationContext());

        // Observe search results from repository
        searchResultsObserver = new Observer<List<Email>>() {
            @Override
            public void onChanged(List<Email> emails) {
                _emails.setValue(Resource.success(emails));
                updateEmptyState(emails);
            }
        };
        emailRepository.getSearchResults().observeForever(searchResultsObserver);

        // Observe loading state
        emailRepository.getIsLoading().observeForever(isLoading -> {
            if (isLoading != null && isLoading) {
                _emails.setValue(Resource.loading(null));
            }
        });

        // Observe errors
        emailRepository.getErrorMessage().observeForever(error -> {
            if (error != null) {
                _emails.setValue(Resource.error(error, null));
            }
        });
    }

    public void setSearchTerm(String searchTerm) {
        _searchTerm.setValue(searchTerm);
        loadEmails(searchTerm);
    }

    public void loadEmails(String searchTerm) {
        _emails.setValue(Resource.loading(null));
        emailRepository.searchEmailsByQuery(searchTerm);
    }

    public void refreshEmails() {
        String currentSearchTerm = _searchTerm.getValue();
        if (currentSearchTerm != null) {
            loadEmails(currentSearchTerm);
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

    private void updateEmptyState(List<Email> emails) {
        _shouldShowEmpty.setValue(emails == null || emails.isEmpty());
    }

    public LiveData<Boolean> getShouldShowEmpty() {
        return _shouldShowEmpty;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (searchResultsObserver != null) {
            emailRepository.getSearchResults().removeObserver(searchResultsObserver);
        }
    }
}
