package com.example.myemailapp.utils;

import java.util.ArrayList;
import java.util.List;
import com.example.myemailapp.model.Email;
public class EmailListManager {

    // Remove email from list (for delete/spam actions)
    public static List<Email> removeEmailFromList(List<Email> emails, String emailId) {
        List<Email> updatedEmails = new ArrayList<>();
        for (Email email : emails) {
            if (!email.getId().equals(emailId)) {
                updatedEmails.add(email);
            }
        }
        return updatedEmails;
    }

    // Update email read status
    public static List<Email> updateEmailReadStatus(List<Email> emails, String emailId, boolean isRead) {
        List<Email> updatedEmails = new ArrayList<>();
        for (Email email : emails) {
            if (email.getId().equals(emailId)) {
                // Create a new email object with updated read status
                Email updatedEmail = new Email(email);
                updatedEmail.setRead(isRead);
                updatedEmails.add(updatedEmail);
            } else {
                updatedEmails.add(email);
            }
        }
        return updatedEmails;
    }

    // Update email star status
    public static List<Email> updateEmailStarStatus(List<Email> emails, String emailId, boolean isStarred) {
        List<Email> updatedEmails = new ArrayList<>();
        for (Email email : emails) {
            if (email.getId().equals(emailId)) {
                // Create a new email object with updated star status
                Email updatedEmail = new Email(email);
                updatedEmail.setStarred(isStarred);
                updatedEmails.add(updatedEmail);
            } else {
                updatedEmails.add(email);
            }
        }
        return updatedEmails;
    }

    // Update email labels
    public static List<Email> updateEmailLabels(List<Email> emails, String emailId, List<String> labels) {
        List<Email> updatedEmails = new ArrayList<>();
        for (Email email : emails) {
            if (email.getId().equals(emailId)) {
                // Create a new email object with updated labels
                Email updatedEmail = new Email(email);
                updatedEmail.setLabels(labels);
                updatedEmails.add(updatedEmail);
            } else {
                updatedEmails.add(email);
            }
        }
        return updatedEmails;
    }
}