package com.example.myemailapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.model.Email;
import com.example.myemailapp.repository.InboxRepository;

import java.util.List;

public class InboxViewModel extends AndroidViewModel {
    private InboxRepository inboxRepository;
    private MutableLiveData<Boolean> shouldShowEmpty = new MutableLiveData<>();

    public InboxViewModel(@NonNull Application application) {
        super(application);
        inboxRepository = new InboxRepository(application.getApplicationContext());
        shouldShowEmpty.setValue(false);
    }

    public LiveData<List<Email>> getInboxEmails() {
        return inboxRepository.getInboxEmails();
    }

    public LiveData<String> getErrorMessage() {
        return inboxRepository.getErrorMessage();
    }

    public LiveData<Boolean> getIsLoading() {
        return inboxRepository.getIsLoading();
    }

    public LiveData<Boolean> getShouldShowEmpty() {
        return shouldShowEmpty;
    }

    public void loadInboxEmails() {
        inboxRepository.loadInboxEmails();
    }

    public void deleteFromInbox(Email email) {
        inboxRepository.deleteFromInbox(email);
    }

    public void markAsRead(Email email) {
        inboxRepository.markAsRead(email);
    }

    public void markAsUnread(Email email) {
        inboxRepository.markAsUnread(email);
    }

    public void markAsSpam(Email email) {
        inboxRepository.markAsSpam(email);
    }

    public void editLabels(Email email, List<String> newLabels) {
        inboxRepository.editLabels(email, newLabels);
    }

    public void updateEmptyState(List<Email> emails) {
        shouldShowEmpty.setValue(emails == null || emails.isEmpty());
    }

//    public Email getEmailById(String emailId) {
//        List<Email> emails = getInboxEmails().getValue();
//        if (emails != null) {
//            for (Email email : emails) {
//                if (email.getId() != null && email.getId().equals(emailId)) {
//                    return email;
//                }
//            }
//        }
//        return null;
//    }
public Email getEmailById(String emailId) {
    if (emailId == null) {
        Log.w("InboxViewModel", "getEmailById called with null emailId");
        return null;
    }
    Log.d("InboxViewModel", "Searching email with ID: hiiiiiii " + emailId);
    // If you have a LiveData<List<Email>> called emailsLiveData
    //List<Email> currentEmails = emailsLiveData.getValue();
    List<Email> currentEmails = getInboxEmails().getValue();
    if (currentEmails != null) {
        for (Email email : currentEmails) {
            Log.d("InboxViewModel", "email with ID: " + email.getId());
            if (emailId.equals(email.getId())) {
                Log.d("InboxViewModel", "Found email with ID: " + emailId);
                return email;
            }
        }
    }

    Log.w("InboxViewModel", "Email not found with ID: " + emailId);
    return null;
}
}