package com.example.myemailapp.repository;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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


public class MailRepository {

    private static final String TAG = "MailRepository";
    private static final String PREF_NAME = "auth";

    private final MailRepository.MailRetrofitApi api;
    private final String authToken;
    private final Context context;
    private final ExecutorService executor;
    private final Handler mainHandler;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();


    public interface MailRetrofitApi {

        class MailRequest {
            public String[] to;
            public String subject;
            public String body;

            public MailRequest(String[] to, String subject, String body) {
                this.to = to;
                this.subject = subject;
                this.body = body;
            }


        }

        @POST("mails")
        Call<Void> sendMail(@Body MailRequest request, @Header("Authorization") String authToken);

        @POST("drafts")
        Call<Void> createDraft(@Body MailRequest request, @Header("Authorization") String authToken);

        @PATCH("drafts/{id}")
        Call<Void> updateDraft(@Path("id") String draftId, @Body MailRequest request, @Header("Authorization") String authToken);

        @DELETE("drafts/{id}")
        Call<Void> deleteDraft(@Path("id") String draftId,/* @Body MailRequest request,*/ @Header("Authorization") String authToken);
    }


    public MailRepository(Context context) {
        this.context = context;
        this.executor = Executors.newFixedThreadPool(2);
        this.mainHandler = new Handler(Looper.getMainLooper());

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.authToken = "Bearer " + prefs.getString("jwt", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(MailRepository.MailRetrofitApi.class);
        isLoading.setValue(false);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }


    public void sendMail(String[] to, String subject, String body) {
        isLoading.setValue(true);
        MailRetrofitApi.MailRequest request = new MailRetrofitApi.MailRequest(to, subject, body);

        api.sendMail(request, authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    Log.d(TAG, "Email sent successfully");
                } else {
                    Log.e(TAG, "Failed to send email: code " + response.code());
                    errorMessage.postValue("Failed to send email: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.postValue(false);
                Log.e(TAG, "Error sending email", t);
                errorMessage.postValue("Failed to send email: " + t.getMessage());
            }
        });
    }


    public void createDraft (String[] to, String subject, String body) {
        isLoading.setValue(true);
        MailRetrofitApi.MailRequest request = new MailRetrofitApi.MailRequest(to, subject, body);

        api.createDraft(request, authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    Log.d(TAG, "Draft created successfully");
                } else {
                    Log.e(TAG, "Failed to create draft: code " + response.code());
                    errorMessage.postValue("Failed to create draft: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.postValue(false);
                Log.e(TAG, "Error creating draft", t);
                errorMessage.postValue("Failed to create draft: " + t.getMessage());
            }
        });
    }


    public void updateDraft (String[] to, String subject, String body, String draftId) {
        isLoading.setValue(true);
        MailRetrofitApi.MailRequest request = new MailRetrofitApi.MailRequest(to, subject, body);

        api.updateDraft(draftId, request, authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.postValue(false);
                if (response.isSuccessful()) {
                    Log.d(TAG, "Draft updated successfully");
                } else {
                    Log.e(TAG, "Failed to update draft: code " + response.code());
                    errorMessage.postValue("Failed to update draft: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.postValue(false);
                Log.e(TAG, "Error updating draft", t);
                errorMessage.postValue("Failed to updating draft: " + t.getMessage());
            }
        });
    }


    public void sendDraftAsMail(String[] to, String subject, String body, String draftId) {
        isLoading.setValue(true);
        MailRetrofitApi.MailRequest request = new MailRetrofitApi.MailRequest(to, subject, body);

        // Step 1: Send the mail
        api.sendMail(request, authToken).enqueue(new RetrofitCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Email sent successfully from draft");

                    // Step 2: Delete the draft
                    api.deleteDraft(draftId, authToken).enqueue(new RetrofitCallback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            isLoading.postValue(false);
                            if (response.isSuccessful()) {
                                Log.d(TAG, "Draft deleted successfully after sending");
                            } else {
                                Log.e(TAG, "Failed to delete draft: code " + response.code());
                                errorMessage.postValue("Failed to delete draft: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            isLoading.postValue(false);
                            Log.e(TAG, "Error deleting draft", t);
                            errorMessage.postValue("Failed to delete draft: " + t.getMessage());
                        }
                    });

                } else {
                    isLoading.postValue(false);
                    Log.e(TAG, "Failed to send email: code " + response.code());
                    errorMessage.postValue("Failed to send email: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.postValue(false);
                Log.e(TAG, "Error sending mail from draft", t);
                errorMessage.postValue("Failed to send email: " + t.getMessage());
            }
        });
    }



    private abstract class RetrofitCallback<T> implements Callback<T> {}
}
