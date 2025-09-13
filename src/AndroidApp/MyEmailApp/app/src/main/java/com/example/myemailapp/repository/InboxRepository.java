package com.example.myemailapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.BuildConfig;
import com.example.myemailapp.model.Email;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
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

public class InboxRepository {
    private static final String TAG = "InboxRepository";
    private static final String PREF_NAME = "auth";
    String baseUrl = BuildConfig.BASE_URL;

    private final InboxRetrofitApi api;
    private final String authToken;
    private final Context context;
    private final ExecutorService executor;
    private final Handler mainHandler;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<List<Email>> inboxEmails = new MutableLiveData<>();
    private final MutableLiveData<List<Email>> searchResults = new MutableLiveData<>();

    // Retrofit API definition
    private interface InboxRetrofitApi {
        @GET("mails")
        Call<List<Email>> getInboxEmails(@Header("Authorization") String authToken);

        @DELETE("inbox/{id}")
        Call<Void> deleteFromInbox(@Path("id") String emailId, @Header("Authorization") String authToken);

        @POST("spam/{id}")
        Call<Void> markAsSpam(@Path("id") String emailId, @Header("Authorization") String authToken);

        @PATCH("inbox/label/{id}")
        Call<Void> editLabels(
                @Path("id") String emailId,
                @Body Map<String, List<String>> wrapper,
                @Header("Authorization") String authToken
        );

        @POST("inbox/read/{id}")
        Call<Void> markAsRead(@Path("id") String emailId, @Header("Authorization") String authToken);

        @POST("inbox/unread/{id}")
        Call<Void> markAsUnread(@Path("id") String emailId, @Header("Authorization") String authToken);

//        @GET("searchAll/label/{labelName}")
//        Call<EmailListWrapper> searchEmailsByLabel(@Header("Authorization") String authToken, @Path("labelName") String labelName);
//@GET("searchAll/label/{labelName}")
//Call<EmailListWrapper> searchEmailsByLabel(@Header("Authorization") String authToken,
//                                           @Path(value = "labelName", encoded = true) String labelName);
@GET("searchAll/label/{labelName}")
Call<EmailListWrapper> searchEmailsByLabel(@Header("Authorization") String authToken,
                                           @Path(value = "labelName", encoded = true) String labelName);

//        @GET("searchAll/label/{labelName}")
//        Call<ResponseBody> searchEmailsByLabel(@Header("Authorization") String authToken,
//                                               @Path(value = "labelName", encoded = true) String labelName);
@GET("mails/search/{query}")
Call<EmailListWrapper> searchEmailsByQuery(
        @Header("Authorization") String authToken,
        @Path(value = "query", encoded = true) String query
);


    }

    public InboxRepository(Context context) {
        this.context = context;
        this.executor = Executors.newFixedThreadPool(2);
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Initialize with empty list
        this.inboxEmails.setValue(Collections.emptyList());

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.authToken = "Bearer " + prefs.getString("jwt", "");
        Log.d("DEBUG", "Base URL: " + BuildConfig.BASE_URL);

        // Use BuildConfig.BASE_URL instead of hardcoded URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL + "/")  // Add trailing slash
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(InboxRetrofitApi.class);
        isLoading.setValue(false);
    }

    public LiveData<List<Email>> getInboxEmails() {
        return inboxEmails;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<Email>> getSearchResults() {
        return searchResults;
    }
    public void loadInboxEmails() {
        isLoading.setValue(true);
        api.getInboxEmails(authToken).enqueue(new RetrofitCallback<List<Email>>() {
            @Override
            public void onResponse(Call<List<Email>> call, Response<List<Email>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Loaded " + response.body().size() + " inbox emails from API");
                    inboxEmails.postValue(response.body());
                } else {
                    Log.e(TAG, "Failed to load inbox emails: code " + response.code());
                    errorMessage.postValue("Failed to load inbox emails: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Email>> call, Throwable t) {
                isLoading.postValue(false);
                Log.e(TAG, "Error loading inbox emails", t);
                errorMessage.postValue("Failed to load inbox emails: " + t.getMessage());
            }
        });
    }

    public void deleteFromInbox(Email email) {
        api.deleteFromInbox(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Deleted email from inbox: " + email.getId());
                    List<Email> currentEmails = inboxEmails.getValue();
                    if (currentEmails != null) {
                        currentEmails.removeIf(e -> e.getId().equals(email.getId()));
                        inboxEmails.postValue(currentEmails);
                    }
                } else {
                    Log.e(TAG, "Failed to delete email: " + response.code());
                    errorMessage.postValue("Failed to delete email");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting email", t);
                errorMessage.postValue("Failed to delete email: " + t.getMessage());
            }
        });
    }

    public void markAsSpam(Email email) {
        api.markAsSpam(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Marked email as spam: " + email.getId());
                    List<Email> currentEmails = inboxEmails.getValue();
                    if (currentEmails != null) {
                        currentEmails.removeIf(e -> e.getId().equals(email.getId()));
                        inboxEmails.postValue(currentEmails);
                    }
                } else {
                    Log.e(TAG, "Failed to mark as spam: " + response.code());
                    errorMessage.postValue("Failed to mark as spam");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error marking as spam", t);
                errorMessage.postValue("Failed to mark as spam: " + t.getMessage());
            }
        });
    }

    public void editLabels(Email email, List<String> newLabels) {
        Map<String, List<String>> body = Collections.singletonMap("labels", newLabels);
        api.editLabels(email.getId(), body, authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Updated labels for email: " + email.getId());
                    List<Email> currentEmails = inboxEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setLabels(newLabels);
                                break;
                            }
                        }
                        inboxEmails.postValue(currentEmails);
                    }
                } else {
                    Log.e(TAG, "Failed to update labels: " + response.code());
                    errorMessage.postValue("Failed to update labels");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error updating labels", t);
                errorMessage.postValue("Failed to update labels: " + t.getMessage());
            }
        });
    }

    public void markAsRead(Email email) {
        api.markAsRead(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Marked email as read: " + email.getId());
                    List<Email> currentEmails = inboxEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setRead(true);
                                break;
                            }
                        }
                        inboxEmails.postValue(currentEmails);
                    }
                } else {
                    Log.e(TAG, "Failed to mark as read: " + response.code());
                    errorMessage.postValue("Failed to mark as read");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error marking as read", t);
                errorMessage.postValue("Failed to mark as read: " + t.getMessage());
            }
        });
    }

    public void markAsUnread(Email email) {
        api.markAsUnread(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Marked email as unread: " + email.getId());
                    List<Email> currentEmails = inboxEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setRead(false);
                                break;
                            }
                        }
                        inboxEmails.postValue(currentEmails);
                    }
                } else {
                    Log.e(TAG, "Failed to mark as unread: " + response.code());
                    errorMessage.postValue("Failed to mark as unread");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error marking as unread", t);
                errorMessage.postValue("Failed to mark as unread: " + t.getMessage());
            }
        });
    }
//    public void searchEmailsByLabel(String labelName) {
//        String token = tokenManager.getToken();
//        if (token == null) {
//            callback.onError("Authentication token not found");
//            return;
//        }
//
//        Call<List<Email>> call = apiService.searchEmailsByLabel("Bearer " + token, labelName);
//        executeCall(call, callback);
//    }

    public class EmailListWrapper {
        @SerializedName("data")  // <-- match the JSON key exactly
        private List<Email> emails;

        public List<Email> getEmails() {
            return emails;
        }
    }



    public void searchEmailsByLabel(String labelName) {
        isLoading.setValue(true);  // Show loading state
        Log.d(TAG, "Auth token: " + authToken); // Log token for debugging
        Log.d(TAG, "Label name: " + labelName);
        String token = authToken;
        if (token == null) {
            isLoading.postValue(false);
            errorMessage.postValue("Authentication token not found");
            Log.e(TAG, "Authentication token not found");
            return;
        }
        try {
            String encodedLabel = URLEncoder.encode(labelName, StandardCharsets.UTF_8.name());
            api.searchEmailsByLabel(authToken, encodedLabel).enqueue(new Callback<EmailListWrapper>() {
                @Override
                public void onResponse(Call<EmailListWrapper> call, Response<EmailListWrapper> response) {
                    isLoading.postValue(false);

                    if (response.isSuccessful() && response.body() != null) {
                        List<Email> emails = response.body().getEmails();
                        if (emails != null) {
                            Log.d(TAG, "Loaded " + emails.size() + " emails with label");
                            searchResults.postValue(emails); // ← ADD THIS LINE
                        } else {
                            Log.e(TAG, "Email list was null in successful response");
                            searchResults.postValue(Collections.emptyList()); // ← ADD THIS LINE
                            errorMessage.postValue("Failed to load emails: no emails found.");
                        }
                    } else {
                        Log.e(TAG, "Failed to search emails: code " + response.code());
                        searchResults.postValue(Collections.emptyList()); // ← ADD THIS LINE
                        errorMessage.postValue("Search failed: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<EmailListWrapper> call, Throwable t) {
                    isLoading.postValue(false);
                    Log.e(TAG, "Error searching emails", t);
                    searchResults.postValue(Collections.emptyList()); // ← ADD THIS LINE
                    errorMessage.postValue("Failed to search emails: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            isLoading.postValue(false);
            searchResults.postValue(Collections.emptyList()); // ← ADD THIS LINE
            errorMessage.postValue("Error: " + e.getMessage());
        }
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
                        if (emails != null) {
                            Log.d(TAG, "Loaded " + emails.size() + " emails from query search");
                            searchResults.postValue(emails);
                        } else {
                            Log.e(TAG, "Email list null in query search response");
                            searchResults.postValue(Collections.emptyList());
                            errorMessage.postValue("No emails found for query.");
                        }
                    } else {
                        Log.e(TAG, "Failed to search emails by query: " + response.code());
                        searchResults.postValue(Collections.emptyList());
                        errorMessage.postValue("Search failed: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<EmailListWrapper> call, Throwable t) {
                    isLoading.postValue(false);
                    Log.e(TAG, "Error searching emails by query", t);
                    searchResults.postValue(Collections.emptyList());
                    errorMessage.postValue("Failed to search emails: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            isLoading.postValue(false);
            searchResults.postValue(Collections.emptyList());
            errorMessage.postValue("Error: " + e.getMessage());
        }
    }


    private abstract class RetrofitCallback<T> implements Callback<T> {}
}