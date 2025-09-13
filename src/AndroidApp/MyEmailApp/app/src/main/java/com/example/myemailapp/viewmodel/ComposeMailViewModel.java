package com.example.myemailapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;


import com.example.myemailapp.repository.MailRepository;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.myemailapp.repository.MailRepository;

public class ComposeMailViewModel extends AndroidViewModel {

    private final MailRepository mailRepository;

    public ComposeMailViewModel(@NonNull Application application) {
        super(application);
        mailRepository = new MailRepository(application.getApplicationContext());
    }

    public void sendMail(String[] to, String subject, String body) {
        mailRepository.sendMail(to, subject, body);
    }

    public void createDraft(String[] to, String subject, String body) {
        mailRepository.createDraft(to, subject, body);
    }

    public void updateDraft(String[] to, String subject, String body, String draftId) {
        mailRepository.updateDraft(to, subject, body, draftId);
    }

    public void sendDraftAsMail(String[] to, String subject, String body, String draftId) {
        mailRepository.sendDraftAsMail(to, subject, body, draftId);
    }
}
