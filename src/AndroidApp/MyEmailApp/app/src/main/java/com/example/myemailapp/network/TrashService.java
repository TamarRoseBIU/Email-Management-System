
package com.example.myemailapp.network;

import com.example.myemailapp.model.Email;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Body;


public class TrashService implements ApiService {

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
                @Body Map<String, List<String>> wrapper,   // or a dedicated DTO class
                @Header("Authorization") String authToken
        );
    }

    private final TrashRetrofitApi api;

    public TrashService(Retrofit retrofit) {
        this.api = retrofit.create(TrashRetrofitApi.class);
    }

    @Override
    public void getItems(String authToken, Callback<List<Email>> callback) {
        api.getTrashEmails(authToken).enqueue(wrap(callback));
    }

    @Override
    public void markAsRead(String emailId, String authToken, Callback<Void> callback) {
        // TODO: Replace with real markAsRead endpoint
        api.restoreFromTrash(emailId, authToken).enqueue(wrap(callback));
    }

    @Override
    public void markAsUnread(String emailId, String authToken, Callback<Void> callback) {
        // TODO: Replace with real markAsUnread endpoint
        api.deleteFromTrash(emailId, authToken).enqueue(wrap(callback));
    }

    @Override
    public void delete(String emailId, String authToken, Callback<Void> callback) {
        api.deleteFromTrash(emailId, authToken).enqueue(wrap(callback));
    }

    @Override
    public void editLabels(String emailId, List<String> labels, String authToken, Callback<Void> callback) {
        Map<String, List<String>> body = Collections.singletonMap("labels", labels);
        api.editLabels(emailId, body, authToken)
                .enqueue(wrap(callback));
    }
    public void restoreFromTrash(String emailId, String authToken, Callback<Void> callback) {
        // TODO: Replace with real markAsUnread endpoint
        api.restoreFromTrash(emailId, authToken).enqueue(wrap(callback));
    }
    public void markAsSpam(String id, String token, Callback<Void> cb) {
        api.markAsSpam(id, token).enqueue(wrap(cb));
    }
    private <T> retrofit2.Callback<T> wrap(Callback<T> cb) {
        return new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) cb.onSuccess(response.body());
                else cb.onError(new RuntimeException("Failed with code: " + response.code()));
            }
            @Override
            public void onFailure(Call<T> call, Throwable t) {
                cb.onError(t);
            }
        };
    }
}