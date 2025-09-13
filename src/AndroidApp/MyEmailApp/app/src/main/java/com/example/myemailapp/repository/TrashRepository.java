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

public class TrashRepository {
    private static final String TAG = "TrashRepository";
    private static final String PREF_NAME = "auth";

    private final TrashRetrofitApi api;
    private final String authToken;
    private final Context context;
    private final ExecutorService executor;
    private final Handler mainHandler;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<List<Email>> trashEmails = new MutableLiveData<>();



    // Retrofit API definition
    private interface TrashRetrofitApi {
        @GET("trash")
        Call<List<Email>> getTrashEmails(@Header("Authorization") String authToken);

        @POST("trash/restore/{id}")
        Call<Void> restoreFromTrash(@Path("id") String emailId, @Header("Authorization") String authToken);

        @DELETE("trash/{id}")
        Call<Void> deleteFromTrash(@Path("id") String emailId, @Header("Authorization") String authToken);

        @POST("spam/{id}")
        Call<Void> markAsSpam(@Path("id") String emailId, @Header("Authorization") String authToken);

        @PATCH("trash/label/{id}")
        Call<Void> editLabels(
                @Path("id") String emailId,
                @Body Map<String, List<String>> wrapper,
                @Header("Authorization") String authToken
        );
    }

    public TrashRepository(Context context) {
        this.context = context;
        this.executor = Executors.newFixedThreadPool(2);
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Initialize with empty list
        this.trashEmails.setValue(Collections.emptyList());

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.authToken = "Bearer " + prefs.getString("jwt", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(TrashRetrofitApi.class);
        isLoading.setValue(false);
    }

    public LiveData<List<Email>> getTrashEmails() {
        return trashEmails;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadTrashEmails() {
        isLoading.setValue(true);
        api.getTrashEmails(authToken).enqueue(new RetrofitCallback<List<Email>>() {
            @Override
            public void onResponse(Call<List<Email>> call, Response<List<Email>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Loaded " + response.body().size() + " trash emails from API");

                    // Update the LiveData directly (no Room for now)
                    trashEmails.postValue(response.body());
                } else {
                    Log.e(TAG, "Failed to load trash emails: code " + response.code());
                    errorMessage.postValue("Failed to load trash emails: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Email>> call, Throwable t) {
                isLoading.postValue(false);
                Log.e(TAG, "Error loading trash emails", t);
                errorMessage.postValue("Failed to load trash emails: " + t.getMessage());
            }
        });
    }

    public void restoreFromTrash(Email email) {
        api.restoreFromTrash(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Restored email from trash: " + email.getId());

                    // Remove email from current list
                    List<Email> currentEmails = trashEmails.getValue();
                    if (currentEmails != null) {
                        currentEmails.removeIf(e -> e.getId().equals(email.getId()));
                        trashEmails.postValue(currentEmails);
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

    public void deleteFromTrash(Email email) {
        api.deleteFromTrash(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Deleted email from trash: " + email.getId());

                    // Remove email from current list
                    List<Email> currentEmails = trashEmails.getValue();
                    if (currentEmails != null) {
                        currentEmails.removeIf(e -> e.getId().equals(email.getId()));
                        trashEmails.postValue(currentEmails);
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

    public void markAsRead(Email email) {
        // Using restore endpoint as placeholder - you mentioned no dedicated endpoint
        api.restoreFromTrash(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Marked email as read: " + email.getId());

                    // Update email in current list
                    List<Email> currentEmails = trashEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setRead(true);
                                break;
                            }
                        }
                        trashEmails.postValue(currentEmails);
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

    }
    public void markAsSpam(Email email) {
        api.markAsSpam(email.getId(), authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Marked email as spam: " + email.getId());

                    // Remove email from current list
                    List<Email> currentEmails = trashEmails.getValue();
                    if (currentEmails != null) {
                        currentEmails.removeIf(e -> e.getId().equals(email.getId()));
                        trashEmails.postValue(currentEmails);
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

                    // Update email in current list
                    List<Email> currentEmails = trashEmails.getValue();
                    if (currentEmails != null) {
                        for (Email e : currentEmails) {
                            if (e.getId().equals(email.getId())) {
                                e.setLabels(newLabels);
                                break;
                            }
                        }
                        trashEmails.postValue(currentEmails);
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

    // Generic Retrofit callback wrapper
    private abstract class RetrofitCallback<T> implements Callback<T> {}
}