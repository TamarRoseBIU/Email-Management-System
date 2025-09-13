package com.example.myemailapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.model.Email;
import com.example.myemailapp.repository.StarredRepository;


import java.util.List;
public class StarredViewModel extends AndroidViewModel {
    private StarredRepository starredRepository;
    private MutableLiveData<Boolean> shouldShowEmpty = new MutableLiveData<>();

    public StarredViewModel(@NonNull Application application) {
        super(application);
        starredRepository = new StarredRepository(application.getApplicationContext());
        shouldShowEmpty.setValue(false);
    }

    public LiveData<List<Email>> getStarredEmails() {
        return starredRepository.getStarredEmails();
    }

    public LiveData<String> getErrorMessage() {
        return starredRepository.getErrorMessage();
    }

    public LiveData<Boolean> getIsLoading() {
        return starredRepository.getIsLoading();
    }

    public LiveData<Boolean> getShouldShowEmpty() {
        return shouldShowEmpty;
    }

    public void loadStarredEmails() {
        starredRepository.loadStarredEmails();
    }

    /*
    public void deleteFromSent(Email email) {
        StarredRepository.deleteFromSent(email);
    }

    public void markAsRead(Email email) {
        StarredRepository.markAsRead(email);
    }

    public void markAsUnread(Email email) {
        StarredRepository.markAsUnread(email);
    }

    public void markAsSpam(Email email) {
        StarredRepository.markAsSpam(email);
    }

    public void editLabels(Email email, List<String> newLabels) {
        sentRepository.editLabels(email, newLabels);
    }

     */

    public void updateEmptyState(List<Email> emails) {
        shouldShowEmpty.setValue(emails == null || emails.isEmpty());
    }

}
