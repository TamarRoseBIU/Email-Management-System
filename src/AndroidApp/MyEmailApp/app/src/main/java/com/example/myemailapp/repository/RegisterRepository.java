//package com.example.myemailapp.repository;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.example.myemailapp.network.AuthService;
//import com.example.myemailapp.network.RegisterRequest;
//import com.example.myemailapp.network.RegisterResponse;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class RegisterRepository {
//
//    private static RegisterRepository instance;
//    private final AuthService api;
//
//    private RegisterRepository() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://api.example.com") // replace with real base URL
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        api = retrofit.create(AuthService.class);
//    }
//
//    public static AuthRepository getInstance() {
//        if (instance == null) {
//            instance = new AuthRepository();
//        }
//        return instance;
//    }
//
//    public LiveData<RegisterResponse> register(RegisterRequest request) {
//        MutableLiveData<RegisterResponse> result = new MutableLiveData<>();
//        api.register(request).enqueue(new Callback<RegisterResponse>() {
//            @Override
//            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    result.postValue(response.body());
//                } else {
//                    RegisterResponse err = new RegisterResponse(false, "Registration failed");
//                    result.postValue(err);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RegisterResponse> call, Throwable t) {
//                RegisterResponse err = new RegisterResponse(false, t.getMessage());
//                result.postValue(err);
//            }
//        });
//        return result;
//    }
//}