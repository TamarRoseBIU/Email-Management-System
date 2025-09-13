package com.example.myemailapp.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemailapp.repository.AuthRepository;

public class LoginViewModel extends AndroidViewModel {
    private AuthRepository authRepository;
    private MutableLiveData<String> validationError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        isLoading.setValue(false);
    }

    public LiveData<AuthRepository.LoginResult> getLoginResult() {
        return authRepository.getLoginResult();
    }

    public LiveData<String> getValidationError() {
        return validationError;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void login(String username, String password) {
        // Validate input
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            validationError.setValue("Username and password cannot be empty");
            return;
        }

        // Clear previous validation errors
        validationError.setValue(null);
        isLoading.setValue(true);

        authRepository.login(username.trim(), password);
    }

    public boolean isUserLoggedIn() {
        return authRepository.isUserLoggedIn();
    }

    public void clearLoginState() {
        authRepository.clearLoginState();
    }

    public void onLoginComplete() {
        isLoading.setValue(false);
    }
}