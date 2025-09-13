package com.example.myemailapp.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.BuildConfig;
import com.example.myemailapp.model.Email;
import com.google.gson.annotations.SerializedName;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class SearchRepository {
    private static final String TAG = "SearchRepository";

    private final SearchApi api;
    private final String authToken;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<List<Email>> searchResults = new MutableLiveData<>();

    interface SearchApi {
        @GET("mails/search/{query}")
        Call<EmailListWrapper> searchEmailsByQuery(
                @Header("Authorization") String authToken,
                @Path(value = "query", encoded = true) String query
        );

        @DELETE("mails/{id}")
        Call<Void> deleteEmail(
                @Header("Authorization") String authToken,
                @Path("id") String emailId
        );

        @PATCH("mails/{id}/read")
        Call<Void> markAsRead(
                @Header("Authorization") String authToken,
                @Path("id") String emailId
        );

        @PATCH("mails/{id}/unread")
        Call<Void> markAsUnread(
                @Header("Authorization") String authToken,
                @Path("id") String emailId
        );

        @PATCH("mails/{id}/spam")
        Call<Void> markAsSpam(
                @Header("Authorization") String authToken,
                @Path("id") String emailId
        );

        @POST("mails/{id}/labels")
        Call<Void> editLabels(
                @Header("Authorization") String authToken,
                @Path("id") String emailId,
                @Body List<String> newLabels
        );
    }

    public SearchRepository(Context context) {
        this.authToken = "Bearer " + context.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("jwt", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(SearchApi.class);
        isLoading.setValue(false);
    }

    public LiveData<List<Email>> getSearchResults() {
        return searchResults;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void searchEmailsByQuery(String query) {
        isLoading.setValue(true);
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
            api.searchEmailsByQuery(authToken, encodedQuery).enqueue(new Callback<EmailListWrapper>() {
                @Override
                public void onResponse(Call<EmailListWrapper> call, Response<EmailListWrapper> response) {
                    isLoading.postValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        List<Email> emails = response.body().getEmails();
                        searchResults.postValue(emails != null ? emails : Collections.emptyList());
                    } else {
                        searchResults.postValue(Collections.emptyList());
                        errorMessage.postValue("Search failed: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<EmailListWrapper> call, Throwable t) {
                    isLoading.postValue(false);
                    searchResults.postValue(Collections.emptyList());
                    errorMessage.postValue("Failed to search emails: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            isLoading.postValue(false);
            searchResults.postValue(Collections.emptyList());
            errorMessage.postValue("Error encoding query: " + e.getMessage());
        }
    }

    // === Operations ===

    public void deleteFromInbox(Email email) {
        api.deleteEmail(authToken, email.getId()).enqueue(defaultCallback("delete"));
    }

    public void markAsRead(Email email) {
        api.markAsRead(authToken, email.getId()).enqueue(defaultCallback("mark as read"));
    }

    public void markAsUnread(Email email) {
        api.markAsUnread(authToken, email.getId()).enqueue(defaultCallback("mark as unread"));
    }

    public void markAsSpam(Email email) {
        api.markAsSpam(authToken, email.getId()).enqueue(defaultCallback("mark as spam"));
    }

    public void editLabels(Email email, List<String> newLabels) {
        api.editLabels(authToken, email.getId(), newLabels).enqueue(defaultCallback("edit labels"));
    }

    // === Common callback for logging ===
    private Callback<Void> defaultCallback(String action) {
        return new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, action + " response: " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, action + " failed: " + t.getMessage());
            }
        };
    }

    public static class EmailListWrapper {
        @SerializedName("data")
        private List<Email> emails;

        public List<Email> getEmails() {
            return emails;
        }
    }
}
