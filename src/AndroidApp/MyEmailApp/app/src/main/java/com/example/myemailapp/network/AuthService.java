//package com.example.myemailapp.network;
//
//import java.util.Map;
//
//import kotlin.jvm.JvmSuppressWildcards;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.GET;
//import retrofit2.http.Multipart;
//import retrofit2.http.POST;
//import retrofit2.http.Part;
//import retrofit2.http.PartMap;
//import retrofit2.http.Path;
//
//public interface AuthService {
//    @POST("tokens")
//    Call<LoginResponse> login(@Body LoginRequest loginRequest);
//
////    @Multipart
////    @POST("users")   // or "auth/register" if your backend lives there
////    Call<RegisterResponse> register(
////            @PartMap Map<String, RequestBody> data,
////            @Part MultipartBody.Part profilePic   // pass null to omit
////    );
//
//    @POST("users")
//    Call<RegisterResponse> register(@Body RegisterRequest body);
//
//
//    @GET("user/username/{username}")
//    Call<UsernameCheckResponse> checkUsername(@Path("username") String username);
//}
package com.example.myemailapp.network;

import java.util.Map;

import kotlin.jvm.JvmSuppressWildcards;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface AuthService {
    @POST("tokens")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // Updated to support multipart form data with optional image
    @Multipart
    @POST("users")
    Call<RegisterResponse> register(
            @PartMap Map<String, RequestBody> data,
            @Part MultipartBody.Part profilePic   // pass null to omit
    );

    // Keep the JSON version as backup if needed
    @POST("users")
    Call<RegisterResponse> registerJson(@Body RegisterRequest body);

    @GET("user/username/{username}")
    Call<UsernameCheckResponse> checkUsername(@Path("username") String username);
}