package com.example.myemailapp.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.example.myemailapp.FileUtils;
import com.example.myemailapp.network.ApiClient;
import com.example.myemailapp.network.AuthService;
import com.example.myemailapp.network.RegisterResponse;
import com.example.myemailapp.network.UsernameCheckResponse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends AndroidViewModel {
    private static final String TAG = "RegisterViewModel";

    // Step constants
    public static final int STEP_NAME = 0;
    public static final int STEP_BIRTH = 1;
    public static final int STEP_USERNAME = 2;
    public static final int STEP_PASSWORD = 3;
    public static final int STEP_PHONE = 4;
    public static final int STEP_PROFILE = 5;
    public static final int TOTAL_STEPS = 6;

    // API
    private AuthService apiService;

    // LiveData for UI state
    private MutableLiveData<Integer> currentStep = new MutableLiveData<>(0);
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isUsernameAvailable = new MutableLiveData<>(false);
    private MutableLiveData<String> usernameError = new MutableLiveData<>();
    private MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>(false);
    private MutableLiveData<Uri> profileImageUri = new MutableLiveData<>();
    private MutableLiveData<String> profileImageName = new MutableLiveData<>();

    // Form data
    private String firstName, lastName, birthDate, gender, username, password, phone;
    private Uri profileUri;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        setupApiService();
    }

    private void setupApiService() {
        apiService = ApiClient.getClient(getApplication()).create(AuthService.class);
    }

    // Getters for LiveData
    public LiveData<Integer> getCurrentStep() {
        return currentStep;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<Boolean> getIsUsernameAvailable() {
        return isUsernameAvailable;
    }

    public LiveData<String> getUsernameError() {
        return usernameError;
    }

    public LiveData<Boolean> getRegistrationSuccess() {
        return registrationSuccess;
    }

    public LiveData<Uri> getProfileImageUri() {
        return profileImageUri;
    }

    public LiveData<String> getProfileImageName() {
        return profileImageName;
    }

    // Navigation methods
    public void nextStep() {
        Integer current = currentStep.getValue();
        if (current != null && current < TOTAL_STEPS - 1) {
            currentStep.setValue(current + 1);
        }
    }

    public void previousStep() {
        Integer current = currentStep.getValue();
        if (current != null && current > 0) {
            currentStep.setValue(current - 1);
        }
    }

    public void goToStep(int step) {
        if (step >= 0 && step < TOTAL_STEPS) {
            currentStep.setValue(step);
        }
    }

    // Validation methods
    public boolean validateName(String firstName, String lastName) {
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();

        if (TextUtils.isEmpty(this.firstName)) {
            errorMessage.setValue("First name is required");
            return false;
        }

        if (!Pattern.matches("^[A-Za-z]+$", this.firstName)) {
            errorMessage.setValue("First name must contain only English letters");
            return false;
        }

        if (TextUtils.isEmpty(this.lastName)) {
            errorMessage.setValue("Last name is required");
            return false;
        }

        if (!Pattern.matches("^[A-Za-z]+$", this.lastName)) {
            errorMessage.setValue("Last name must contain only English letters");
            return false;
        }

        return true;
    }

    public boolean validateBirthAndGender(String birthDate, String gender) {
        this.birthDate = birthDate.trim();
        this.gender = gender;

        if (TextUtils.isEmpty(this.birthDate)) {
            errorMessage.setValue("Birth date is required");
            return false;
        }

        // Check if birth date is in the future
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date birth = sdf.parse(this.birthDate);
            if (birth != null && birth.after(new Date())) {
                errorMessage.setValue("Birth date must be in the past");
                return false;
            }
        } catch (Exception e) {
            errorMessage.setValue("Invalid date format");
            return false;
        }

        if (TextUtils.isEmpty(this.gender)) {
            errorMessage.setValue("Please select a gender");
            return false;
        }

        return true;
    }

    public boolean validateUsername(String username) {
        this.username = username.trim();

        if (TextUtils.isEmpty(this.username)) {
            errorMessage.setValue("Username is required");
            return false;
        }

        if (this.username.length() < 3) {
            errorMessage.setValue("Username must be at least 3 characters");
            return false;
        }

        if (!Pattern.matches("^[a-zA-Z0-9]+$", this.username)) {
            errorMessage.setValue("Username must contain only English letters and numbers");
            return false;
        }

        return true;
    }

    public boolean validatePassword(String password, String confirmPassword) {
        this.password = password;

        if (TextUtils.isEmpty(this.password)) {
            errorMessage.setValue("Password is required");
            return false;
        }

        if (this.password.length() < 6) {
            errorMessage.setValue("Password must be at least 6 characters");
            return false;
        }

        if (this.password.contains(" ")) {
            errorMessage.setValue("Password cannot contain spaces");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            errorMessage.setValue("Please confirm your password");
            return false;
        }

        if (!this.password.equals(confirmPassword)) {
            errorMessage.setValue("Passwords must match");
            return false;
        }

        return true;
    }

    public boolean validatePhone(String phone) {
        this.phone = phone.trim();

        if (TextUtils.isEmpty(this.phone)) {
            errorMessage.setValue("Phone number is required");
            return false;
        }

        if (!Pattern.matches("^05\\d{8}$", this.phone)) {
            errorMessage.setValue("Phone number must be 10 digits long and start with '05'");
            return false;
        }

        return true;
    }

    public boolean validateProfile(Uri profileUri) {
        this.profileUri = profileUri;

        // Profile picture is optional, so always return true
        // But validate file type if selected
        if (profileUri != null) {
            String fileName = FileUtils.getFileName(getApplication(), profileUri);
            if (fileName != null && !fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|webp)$")) {
                errorMessage.setValue("Please upload a valid image file (jpg, jpeg, png, gif, webp)");
                return false;
            }
        }
        return true;
    }

    // Username availability check
    public void checkUsernameAvailability(String username) {
        if (!validateUsername(username)) {
            return;
        }

        isLoading.setValue(true);
        usernameError.setValue("");

        Call<UsernameCheckResponse> call = apiService.checkUsername(username);
        call.enqueue(new Callback<UsernameCheckResponse>() {
            @Override
            public void onResponse(Call<UsernameCheckResponse> call, Response<UsernameCheckResponse> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isExists()) {
                        isUsernameAvailable.setValue(false);
                        usernameError.setValue("Username already exists. Please choose a different one");
                    } else {
                        isUsernameAvailable.setValue(true);
                        usernameError.setValue("");
                    }
                } else {
                    isUsernameAvailable.setValue(false);
                    usernameError.setValue("Failed to validate username. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<UsernameCheckResponse> call, Throwable t) {
                isLoading.setValue(false);
                isUsernameAvailable.setValue(false);
                usernameError.setValue("Network error. Please try again.");
                Log.e(TAG, "Username validation failed", t);
            }
        });
    }

    // Profile image handling


    public boolean isValidImageFile(Uri uri) {
        try {
            String mimeType = getApplication().getContentResolver().getType(uri);
            if (mimeType != null && mimeType.startsWith("image/")) {
                return true;
            }

            // Fallback: check file extension
            String fileName = FileUtils.getFileName(getApplication(), uri);
            if (fileName != null) {
                String extension = fileName.toLowerCase();
                return extension.endsWith(".jpg") || extension.endsWith(".jpeg") ||
                        extension.endsWith(".png") || extension.endsWith(".gif") ||
                        extension.endsWith(".webp");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error validating image file", e);
        }
        return false;
    }



    // Setters for form data
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setProfileImage(Uri uri) {
        this.profileUri = uri;
        profileImageUri.setValue(uri);

        if (uri != null) {
            String fileName = FileUtils.getFileName(getApplication(), uri);
            profileImageName.setValue("Image selected: " + fileName);
        } else {
            profileImageName.setValue("");
        }
    }
    // Registration
    public void attemptRegister() {
        // Check username availability one more time before proceeding
        Boolean usernameAvailable = isUsernameAvailable.getValue();
        if (usernameAvailable == null || !usernameAvailable) {
            errorMessage.setValue("Please choose a different username");
            currentStep.setValue(STEP_USERNAME);
            return;
        }

        // Create text parts for multipart request
        Map<String, RequestBody> data = new HashMap<>();
        data.put("firstName", RequestBody.create(MediaType.parse("text/plain"), firstName));
        data.put("lastName", RequestBody.create(MediaType.parse("text/plain"), lastName));
        data.put("username", RequestBody.create(MediaType.parse("text/plain"), username));
        data.put("password", RequestBody.create(MediaType.parse("text/plain"), password));
        data.put("phoneNumber", RequestBody.create(MediaType.parse("text/plain"), phone));
        data.put("birthDate", RequestBody.create(MediaType.parse("text/plain"), birthDate));
        data.put("gender", RequestBody.create(MediaType.parse("text/plain"), gender));

        // Handle profile picture if selected
        MultipartBody.Part profilePicPart = null;
        if (profileUri != null) {
            try {
                String filePath = FileUtils.getPath(getApplication(), profileUri);
                if (filePath != null) {
                    File file = new File(filePath);
                    String mimeType = getApplication().getContentResolver().getType(profileUri);
                    if (mimeType == null) {
                        mimeType = "image/jpeg"; // default
                    }

                    RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
                    profilePicPart = MultipartBody.Part.createFormData("profilePic", file.getName(), requestFile);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error preparing image for upload", e);
                errorMessage.setValue("Error preparing image for upload");
                return;
            }
        }

        // Show loading indicator
        isLoading.setValue(true);

        // Send registration request
        Call<RegisterResponse> call = apiService.register(data, profilePicPart);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    successMessage.setValue(message);
                    registrationSuccess.setValue(true);
                } else {
                    String errorMsg = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Registration request failed", t);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    // Clear messages
    public void clearMessages() {
        errorMessage.setValue("");
        successMessage.setValue("");
        usernameError.setValue("");
    }
}