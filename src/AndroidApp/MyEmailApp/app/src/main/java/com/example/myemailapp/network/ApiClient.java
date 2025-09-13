//package com.example.myemailapp.network;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//public class ApiClient {
//    private static final String BASE_URL = "http://192.168.1.100:8080/api/"; // your machine’s IP
//
//    private static Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build();
//
//    public static ApiService getService() {
//        return retrofit.create(ApiService.class);
//    }
//}
package com.example.myemailapp.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myemailapp.BuildConfig;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getClient(Context ctx) {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            // Interceptor reads the token from prefs
            Interceptor authInterceptor = chain -> {
                SharedPreferences prefs = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE);
                String token = prefs.getString("jwt", null);
                Request req = chain.request();
                if (token != null) {
                    req = req.newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                }
                return chain.proceed(req);
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL + "/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static AuthService getService(Context ctx) {
        return getClient(ctx).create(AuthService.class);
    }
}
