
/*
 * network/ApiService.java
 */
package com.example.myemailapp.network;

import com.example.myemailapp.model.Email;
import java.util.List;

/**
 * Generic email API operations.
 */
public interface ApiService {
    // Fetch list of items (e.g. inbox, trash)
    void getItems(String authToken, Callback<List<Email>> callback);

    // Mark as read/unread
    void markAsRead(String emailId, String authToken, Callback<Void> callback);
    void markAsUnread(String emailId, String authToken, Callback<Void> callback);

    // Delete or restore
    void delete(String emailId, String authToken, Callback<Void> callback);

    // Edit labels (e.g. archive, star)
    void editLabels(String emailId, List<String> labels, String authToken, Callback<Void> callback);

    /**
     * Simple callback to surface Retrofit responses.
     */
    interface Callback<T> {
        void onSuccess(T result);
        void onError(Throwable t);
    }
}