package com.example.myemailapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.model.Email;
import com.example.myemailapp.repository.TrashRepository;

import java.util.List;

public class TrashViewModel extends AndroidViewModel {
    private TrashRepository trashRepository;
    private MutableLiveData<Boolean> shouldShowEmpty = new MutableLiveData<>();

    // Constructor that takes Application (AndroidViewModel requirement)
    public TrashViewModel(@NonNull Application application) {
        super(application);
        trashRepository = new TrashRepository(application.getApplicationContext());
        shouldShowEmpty.setValue(false);
    }

    public LiveData<List<Email>> getTrashEmails() {
        return trashRepository.getTrashEmails();
    }

    public LiveData<String> getErrorMessage() {
        return trashRepository.getErrorMessage();
    }

    public LiveData<Boolean> getIsLoading() {
        return trashRepository.getIsLoading();
    }

    public LiveData<Boolean> getShouldShowEmpty() {
        return shouldShowEmpty;
    }

    public void loadTrashEmails() {
        trashRepository.loadTrashEmails();
    }

    public void restoreFromTrash(Email email) {
        trashRepository.restoreFromTrash(email);
    }

    public void deleteFromTrash(Email email) {
        trashRepository.deleteFromTrash(email);
    }

    public void markAsRead(Email email) {
        trashRepository.markAsRead(email);
    }

    public void markAsUnread(Email email) {
        trashRepository.markAsUnread(email);
    }

    public void markAsSpam(Email email) {
        trashRepository.markAsSpam(email);
    }

    public void editLabels(Email email, List<String> newLabels) {
        trashRepository.editLabels(email, newLabels);
    }

    public void updateEmptyState(List<Email> emails) {
        shouldShowEmpty.setValue(emails == null || emails.isEmpty());
    }
}