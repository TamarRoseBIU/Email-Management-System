package com.example.myemailapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.model.User;
import com.example.myemailapp.network.ApiClient;
import com.example.myemailapp.network.AuthService;
import com.example.myemailapp.network.LoginRequest;
import com.example.myemailapp.network.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private static final String PREF_NAME = "auth";

    private AuthService authService;
    private SharedPreferences sharedPreferences;
    private Context context;

    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public AuthRepository(Context context) {
        this.context = context;
        this.authService = ApiClient.getService(context);
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);

        authService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        String jwt = loginResponse.getToken();
                        String message = loginResponse.getMessage();
                        User user = loginResponse.getUserJson();

                        Log.d(TAG, "Login success: " + message + ", token: " + jwt);
                        if (user != null) {
                            Log.d(TAG, "User: " + user.getUsername()
                                    + " (" + user.getFirstName() + " " + user.getLastName() + ")");
                        }

                        saveUserData(jwt, user);
                        loginResult.postValue(new LoginResult(true, message, user));

                    } else {
                        String errorMsg = "Login failed";
                        String cleanError = errorMsg;
                        if (response.errorBody() != null) {
                            try {
                                errorMsg = response.errorBody().string();
                                cleanError = errorMsg.replaceAll("[{}\"]", "")
                                        .replace("error:", "")
                                        .trim();
                            } catch (Exception e) {
                                errorMsg = "Login failed with code: " + response.code();
                            }
                        }
                        Log.e(TAG, "Login failed: " + response.code() + " " + errorMsg);
                        loginResult.postValue(new LoginResult(false, cleanError, null));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing login response", e);
                    loginResult.postValue(new LoginResult(false, "Error processing response", null));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login request failed", t);
                loginResult.postValue(new LoginResult(false, "Network error: " + t.getMessage(), null));
            }
        });
    }

    private void saveUserData(String jwt, User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jwt", jwt);

        if (user != null) {
            editor.putString("username", user.getUsername());
            editor.putString("firstName", user.getFirstName());
            editor.putString("lastName", user.getLastName());
            editor.putString("profilePic", user.getProfilePic());
            editor.putString("phoneNumber", user.getPhoneNumber());
            editor.putString("birthDate", user.getBirthDate());
            editor.putString("gender", user.getGender());
        }

        editor.apply();
    }

    public boolean isUserLoggedIn() {
        String jwt = sharedPreferences.getString("jwt", "");
        String username = sharedPreferences.getString("username", "");
        return !jwt.isEmpty() && !username.isEmpty();
    }

    public void clearLoginState() {
        sharedPreferences.edit().clear().apply();
        Log.d(TAG, "Login state cleared");
    }

    public static class LoginResult {
        private boolean success;
        private String message;
        private User user;

        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }
}