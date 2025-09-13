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

public class DraftsRepository {
    private static final String TAG = "DraftsRepository";
    private static final String PREF_NAME = "auth";

    private final DraftsRetrofitApi api;
    private final String authToken;
    private final Context context;
    private final ExecutorService executor;
    private final Handler mainHandler;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<List<Email>> drafts = new MutableLiveData<>();

    // Retrofit API definition
    private interface DraftsRetrofitApi {
        @GET("drafts")
        Call<List<Email>> getDrafts(@Header("Authorization") String authToken);
    }

    public DraftsRepository(Context context) {
        this.context = context;
        this.executor = Executors.newFixedThreadPool(2);
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Initialize with empty list
        this.drafts.setValue(Collections.emptyList());

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.authToken = "Bearer " + prefs.getString("jwt", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(DraftsRetrofitApi.class);
        isLoading.setValue(false);
    }

    public LiveData<List<Email>> getDrafts() {
        return drafts;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadDrafts() {
        isLoading.setValue(true);
        api.getDrafts(authToken).enqueue(new RetrofitCallback<List<Email>>() {
            @Override
            public void onResponse(Call<List<Email>> call, Response<List<Email>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Loaded " + response.body().size() + " drafts from API");
                    drafts.postValue( response.body());
                } else {
                    Log.e(TAG, "Failed to load drafts: code " + response.code());
                    errorMessage.postValue("Failed to load drafts: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Email>> call, Throwable t) {
                isLoading.postValue(false);
                Log.e(TAG, "Error loading drafts", t);
                errorMessage.postValue("Failed to load drafts: " + t.getMessage());
            }
        });
    }

/*
    public void editLabels(Email email, List<String> newLabels) {
        Map<String, List<String>> body = Collections.singletonMap("labels", newLabels);
        api.editLabels(email.getId(), body, authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Updated labels for email: " + email.getId());
                    List<Email> currentEmails = sentEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setLabels(newLabels);
                                break;
                            }
                        }
                        sentEmails.postValue(currentEmails);
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
                    List<Email> currentEmails = sentEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setRead(true);
                                break;
                            }
                        }
                        sentEmails.postValue(currentEmails);
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
                    List<Email> currentEmails = sentEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setRead(false);
                                break;
                            }
                        }
                        sentEmails.postValue(currentEmails);
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

 */

    private abstract class RetrofitCallback<T> implements Callback<T> {}
}