package com.example.myemailapp.data.database.network;

import android.content.Context;
import android.util.Log;

import com.example.myemailapp.BuildConfig;
import com.example.myemailapp.data.database.entity.Label;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

public class LabelApiService {
    private static final String TAG = "LabelApiService";
    private static final int TIMEOUT_SECONDS = 30;

    private final OkHttpClient client;
    private final Gson gson;
    private final String baseUrl;
    private final String token;

    public LabelApiService(Context context, String token) {
        this.token = token;
        this.gson = new Gson();

        // Get base URL from BuildConfig or use default
        //String port = "8080"; // You can make this configurable
        this.baseUrl = BuildConfig.BASE_URL + "/labels";

        this.client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    /**
     * Fetch all labels from the API
     */
    public void fetchLabels(ApiCallback<List<Label>> callback) {
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch labels", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        Log.e(TAG, "Failed to fetch labels: " + response.code() + " - " + errorBody);
                        callback.onError("Server error: " + response.code());
                        return;
                    }

                    String responseBody = response.body().string();
                    Type listType = new TypeToken<List<Label>>(){}.getType();
                    List<Label> labels = gson.fromJson(responseBody, listType);

                    Log.d(TAG, "Successfully fetched " + labels.size() + " labels");
                    callback.onSuccess(labels);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing labels response", e);
                    callback.onError("Error parsing response: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Create a new label
     */
    public void createLabel(Label label, ApiCallback<Label> callback) {
        // Create request body with just the name
        String jsonBody = gson.toJson(new LabelRequest(label.getName()));

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to create label", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body() != null ? response.body().string() : "";

                    if (response.code() == 409) {
                        Log.w(TAG, "Label already exists: " + responseBody);
                        callback.onError("Label already exists");
                        return;
                    }

                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Failed to create label: " + response.code() + " - " + responseBody);
                        callback.onError("Server error: " + response.code());
                        return;
                    }

                    Label createdLabel = gson.fromJson(responseBody, Label.class);
                    Log.d(TAG, "Successfully created label: " + createdLabel.getName());
                    callback.onSuccess(createdLabel);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing create label response", e);
                    callback.onError("Error parsing response: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Update an existing label
     */
    public void updateLabel(String labelId, Label label, ApiCallback<Label> callback) {
        // Create request body with just the name
        String jsonBody = gson.toJson(new LabelRequest(label.getName()));

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(baseUrl + "/" + labelId)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to update label", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body() != null ? response.body().string() : "";

                    if (response.code() == 409) {
                        Log.w(TAG, "Label name already exists: " + responseBody);
                        callback.onError("Label name already exists");
                        return;
                    }

                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Failed to update label: " + response.code() + " - " + responseBody);
                        callback.onError("Server error: " + response.code());
                        return;
                    }

                    Label updatedLabel = gson.fromJson(responseBody, Label.class);
                    Log.d(TAG, "Successfully updated label: " + updatedLabel.getName());
                    callback.onSuccess(updatedLabel);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing update label response", e);
                    callback.onError("Error parsing response: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Delete a label
     */
    public void deleteLabel(String labelId, ApiCallback<Void> callback) {
        Request request = new Request.Builder()
                .url(baseUrl + "/" + labelId)
                .addHeader("Authorization", "Bearer " + token)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to delete label", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        Log.e(TAG, "Failed to delete label: " + response.code() + " - " + errorBody);
                        callback.onError("Server error: " + response.code());
                        return;
                    }

                    Log.d(TAG, "Successfully deleted label: " + labelId);
                    callback.onSuccess(null);

                } catch (Exception e) {
                    Log.e(TAG, "Error processing delete label response", e);
                    callback.onError("Error processing response: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Helper class for request body
     */
    private static class LabelRequest {
        private final String name;

        public LabelRequest(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}