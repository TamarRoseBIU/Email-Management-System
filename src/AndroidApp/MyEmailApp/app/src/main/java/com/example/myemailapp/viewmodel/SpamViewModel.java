package com.example.myemailapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.model.Email;
import com.example.myemailapp.repository.SpamRepository;

import java.util.List;

public class SpamViewModel extends AndroidViewModel {
    private SpamRepository spamRepository;
    private MutableLiveData<Boolean> shouldShowEmpty = new MutableLiveData<>();

    public SpamViewModel(@NonNull Application application) {
        super(application);
        spamRepository = new SpamRepository(application.getApplicationContext());
        shouldShowEmpty.setValue(false);
    }

    public LiveData<List<Email>> getSpamEmails() {
        return spamRepository.getSpamEmails();
    }

    public LiveData<String> getErrorMessage() {
        return spamRepository.getErrorMessage();
    }

    public LiveData<Boolean> getIsLoading() {
        return spamRepository.getIsLoading();
    }

    public LiveData<Boolean> getShouldShowEmpty() {
        return shouldShowEmpty;
    }

    public void loadSpamEmails() {
        spamRepository.loadSpamEmails();
    }

    public void deleteSpamEmail(Email email) {
        spamRepository.deleteSpamEmail(email);
    }

    public void restoreFromSpam(Email email) {
        spamRepository.restoreFromSpam(email);
    }

    public void markAsRead(Email email) {
        spamRepository.markAsRead(email);
    }

    public void markAsUnread(Email email) {
        spamRepository.markAsUnread(email);
    }

    public void starEmail(Email email) {
        spamRepository.starEmail(email);
    }

    public void unstarEmail(Email email) {
        spamRepository.unstarEmail(email);
    }

    public void addToSpamFromInbox(Email email) {
        spamRepository.addToSpamFromInbox(email);
    }

    public void addToSpamFromMails(Email email) {
        spamRepository.addToSpamFromMails(email);
    }

    public void addToSpamFromDraft(Email email) {
        spamRepository.addToSpamFromDraft(email);
    }

    public void addToSpamFromTrash(Email email) {
        spamRepository.addToSpamFromTrash(email);
    }

    public void editLabels(Email email, List<String> newLabels) {
        spamRepository.updateLabels(email, newLabels);
    }

    public void updateEmptyState(List<Email> emails) {
        shouldShowEmpty.setValue(emails == null || emails.isEmpty());
    }
}
