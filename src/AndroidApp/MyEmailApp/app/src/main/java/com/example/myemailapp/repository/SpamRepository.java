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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

public class SpamRepository {
    private static final String TAG = "SpamRepository";
    private static final String PREF_NAME = "auth";

    private final SpamRetrofitApi api;
    private final String authToken;
    private final Context context;
    private final ExecutorService executor;
    private final Handler mainHandler;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<List<Email>> spamEmails = new MutableLiveData<>();

    private interface SpamRetrofitApi {
        @GET("spam")
        Call<List<Email>> getSpamEmails(@Header("Authorization") String authToken);

        @GET("spam/{id}")
        Call<Email> getSpamEmailById(@Path("id") String emailId, @Header("Authorization") String authToken);

        @DELETE("spam/{id}")
        Call<Void> deleteSpamEmail(@Path("id") String emailId, @Header("Authorization") String authToken);

        @POST("spam/restore/{id}")
        Call<Void> restoreFromSpam(@Path("id") String emailId, @Header("Authorization") String authToken);

        @POST("spam/from-inbox/{id}")
        Call<Void> addToSpamFromInbox(@Path("id") String emailId, @Header("Authorization") String authToken);

        @POST("spam/from-mails/{id}")
        Call<Void> addToSpamFromMails(@Path("id") String emailId, @Header("Authorization") String authToken);

        @POST("spam/from-drafts/{id}")
        Call<Void> addToSpamFromDraft(@Path("id") String emailId, @Header("Authorization") String authToken);

        @POST("spam/from-trash/{id}")
        Call<Void> addToSpamFromTrash(@Path("id") String emailId, @Header("Authorization") String authToken);

        @PATCH("spam/read/{id}")
        Call<Void> markRead(@Path("id") String emailId, @Header("Authorization") String authToken);

        @PATCH("spam/unread/{id}")
        Call<Void> markUnread(@Path("id") String emailId, @Header("Authorization") String authToken);

        @PATCH("spam/label/{id}")
        Call<Void> updateLabels(@Path("id") String emailId, @Body Map<String, List<String>> labelsWrapper, @Header("Authorization") String authToken);

        @PATCH("spam/star/{id}")
        Call<Void> starEmail(@Path("id") String emailId, @Header("Authorization") String authToken);

        @PATCH("spam/unstar/{id}")
        Call<Void> unstarEmail(@Path("id") String emailId, @Header("Authorization") String authToken);
    }

    public SpamRepository(Context context) {
        this.context = context;
        this.executor = Executors.newFixedThreadPool(2);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.spamEmails.setValue(Collections.emptyList());

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.authToken = "Bearer " + prefs.getString("jwt", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(SpamRetrofitApi.class);
        isLoading.setValue(false);
    }

    public LiveData<List<Email>> getSpamEmails() {
        return spamEmails;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadSpamEmails() {
        isLoading.setValue(true);
        api.getSpamEmails(authToken).enqueue(new RetrofitCallback<List<Email>>() {
            @Override
            public void onResponse(Call<List<Email>> call, Response<List<Email>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Loaded " + response.body().size() + " spam emails");
                    spamEmails.postValue(response.body());
                } else {
                    Log.e(TAG, "Failed to load spam emails: code " + response.code());
                    errorMessage.postValue("Failed to load spam emails: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Email>> call, Throwable t) {
                isLoading.postValue(false);
                Log.e(TAG, "Error loading spam emails", t);
                errorMessage.postValue("Failed to load spam emails: " + t.getMessage());
            }
        });
    }

    public void deleteSpamEmail(Email email) {
        api.deleteSpamEmail(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Deleted spam email: " + email.getId());
                    List<Email> currentEmails = spamEmails.getValue();
                    if (currentEmails != null) {
                        currentEmails.removeIf(e -> e.getId().equals(email.getId()));
                        spamEmails.postValue(currentEmails);
                    }
                } else {
                    Log.e(TAG, "Failed to delete spam email: " + response.code());
                    errorMessage.postValue("Failed to delete spam email");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting spam email", t);
                errorMessage.postValue("Failed to delete spam email: " + t.getMessage());
            }
        });
    }

    public void restoreFromSpam(Email email) {
        api.restoreFromSpam(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Restored email from spam: " + email.getId());
                    List<Email> currentEmails = spamEmails.getValue();
                    if (currentEmails != null) {
                        currentEmails.removeIf(e -> e.getId().equals(email.getId()));
                        spamEmails.postValue(currentEmails);
                    }
                } else {
                    Log.e(TAG, "Failed to restore email: " + response.code());
                    errorMessage.postValue("Failed to restore email");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error restoring email", t);
                errorMessage.postValue("Failed to restore email: " + t.getMessage());
            }
        });
    }

    public void addToSpamFromInbox(Email email) {
        api.addToSpamFromInbox(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Added email to spam from inbox: " + email.getId());
                } else {
                    Log.e(TAG, "Failed to add to spam from inbox: " + response.code());
                    errorMessage.postValue("Failed to add to spam from inbox");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error adding to spam from inbox", t);
                errorMessage.postValue("Failed to add to spam from inbox: " + t.getMessage());
            }
        });
    }

    public void addToSpamFromMails(Email email) {
        api.addToSpamFromMails(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Added email to spam from mails: " + email.getId());
                } else {
                    Log.e(TAG, "Failed to add to spam from mails: " + response.code());
                    errorMessage.postValue("Failed to add to spam from mails");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error adding to spam from mails", t);
                errorMessage.postValue("Failed to add to spam from mails: " + t.getMessage());
            }
        });
    }

    public void addToSpamFromDraft(Email email) {
        api.addToSpamFromDraft(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Added email to spam from draft: " + email.getId());
                } else {
                    Log.e(TAG, "Failed to add to spam from draft: " + response.code());
                    errorMessage.postValue("Failed to add to spam from draft");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error adding to spam from draft", t);
                errorMessage.postValue("Failed to add to spam from draft: " + t.getMessage());
            }
        });
    }

    public void addToSpamFromTrash(Email email) {
        api.addToSpamFromTrash(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Added email to spam from trash: " + email.getId());
                } else {
                    Log.e(TAG, "Failed to add to spam from trash: " + response.code());
                    errorMessage.postValue("Failed to add to spam from trash");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error adding to spam from trash", t);
                errorMessage.postValue("Failed to add to spam from trash: " + t.getMessage());
            }
        });
    }

    public void markAsRead(Email email) {
        api.markRead(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Marked spam email as read: " + email.getId());
                    List<Email> currentEmails = spamEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setRead(true);
                                break;
                            }
                        }
                        spamEmails.postValue(currentEmails);
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
        api.markUnread(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Marked spam email as unread: " + email.getId());
                    List<Email> currentEmails = spamEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setRead(false);
                                break;
                            }
                        }
                        spamEmails.postValue(currentEmails);
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

    public void starEmail(Email email) {
        api.starEmail(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Starred spam email: " + email.getId());
                    updateStarState(email, true);
                } else {
                    Log.e(TAG, "Failed to star email: " + response.code());
                    errorMessage.postValue("Failed to star email");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error starring email", t);
                errorMessage.postValue("Failed to star email: " + t.getMessage());
            }
        });
    }

    public void unstarEmail(Email email) {
        api.unstarEmail(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Unstarred spam email: " + email.getId());
                    updateStarState(email, false);
                } else {
                    Log.e(TAG, "Failed to unstar email: " + response.code());
                    errorMessage.postValue("Failed to unstar email");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error unstarring email", t);
                errorMessage.postValue("Failed to unstar email: " + t.getMessage());
            }
        });
    }

    public void updateLabels(Email email, List<String> newLabels) {
        Map<String, List<String>> body = Collections.singletonMap("labels", newLabels);
        api.updateLabels(email.getId(), body, authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Updated labels for spam email: " + email.getId());
                    List<Email> currentEmails = spamEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setLabels(newLabels);
                                break;
                            }
                        }
                        spamEmails.postValue(currentEmails);
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

    private void updateStarState(Email email, boolean isStarred) {
        List<Email> currentEmails = spamEmails.getValue();
        if (currentEmails != null) {
            for (Email e : currentEmails) {
                if (e.getId().equals(email.getId())) {
                    e.setStarred(isStarred);
                    break;
                }
            }
            spamEmails.postValue(currentEmails);
        }
    }

    private abstract class RetrofitCallback<T> implements Callback<T> {}
}
