package com.example.myemailapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.model.Email;
import com.example.myemailapp.repository.SentRepository;

import java.util.List;

public class SentViewModel extends AndroidViewModel {
    private SentRepository sentRepository;
    private MutableLiveData<Boolean> shouldShowEmpty = new MutableLiveData<>();

    public SentViewModel(@NonNull Application application) {
        super(application);
        sentRepository = new SentRepository(application.getApplicationContext());
        shouldShowEmpty.setValue(false);
    }

    public LiveData<List<Email>> getSentEmails() {
        return sentRepository.getSentEmails();
    }

    public LiveData<String> getErrorMessage() {
        return sentRepository.getErrorMessage();
    }

    public LiveData<Boolean> getIsLoading() {
        return sentRepository.getIsLoading();
    }

    public LiveData<Boolean> getShouldShowEmpty() {
        return shouldShowEmpty;
    }

    public void loadSentEmails() {
        sentRepository.loadSentEmails();
    }

    public void deleteFromSent(Email email) {
        sentRepository.deleteFromSent(email);
    }

    public void markAsRead(Email email) {
        sentRepository.markAsRead(email);
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

    public void updateEmptyState(List<Email> emails) {
        shouldShowEmpty.setValue(emails == null || emails.isEmpty());
    }
}