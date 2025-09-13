package com.example.myemailapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.model.Email;
import com.example.myemailapp.repository.DraftsRepository;

import java.util.List;

public class DraftsViewModel extends AndroidViewModel {
    private DraftsRepository draftsRepository;
    private MutableLiveData<Boolean> shouldShowEmpty = new MutableLiveData<>();

    public DraftsViewModel(@NonNull Application application) {
        super(application);
        draftsRepository = new DraftsRepository(application.getApplicationContext());
        shouldShowEmpty.setValue(false);
    }

    public LiveData<List<Email>> getDrafts() {
        return draftsRepository.getDrafts();
    }

    public LiveData<String> getErrorMessage() {
        return draftsRepository.getErrorMessage();
    }

    public LiveData<Boolean> getIsLoading() {
        return draftsRepository.getIsLoading();
    }

    public LiveData<Boolean> getShouldShowEmpty() {
        return shouldShowEmpty;
    }

    public void loadDrafts() {
        draftsRepository.loadDrafts();
    }

    /*
    public void deleteFromSent(Email email) {
        draftsRepository.deleteFromSent(email);
    }

    public void markAsRead(Email email) {
        draftsRepository.markAsRead(email);
    }

    public void markAsUnread(Email email) {
        sentRepository.markAsUnread(email);
    }

    public void markAsSpam(Email email) {
        sentRepository.markAsSpam(email);
    }

    public void editLabels(Email email, List<String> newLabels) {
        sentRepository.editLabels(email, newLabels);
    }
*/
    public void updateEmptyState(List<Email> emails) {
        shouldShowEmpty.setValue(emails == null || emails.isEmpty());
    }
}