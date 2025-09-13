//package com.example.myemailapp.ui.login;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//import android.widget.TextView;
//import android.widget.CheckBox;
//import android.animation.ObjectAnimator;
//import android.animation.AnimatorSet;
//import android.view.animation.DecelerateInterpolator;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.example.myemailapp.R;
//import com.example.myemailapp.network.ApiClient;
//import com.example.myemailapp.network.AuthService;
//import com.example.myemailapp.network.RegisterRequest;
//import com.example.myemailapp.network.RegisterResponse;
//import com.example.myemailapp.network.UsernameCheckResponse;
//import com.example.myemailapp.FileUtils;
//import com.google.gson.Gson;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//import java.util.regex.Pattern;
//import android.Manifest;
//import android.content.pm.PackageManager;
//
//import android.os.Build;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//// Add these constants
//
//
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class RegisterActivity extends AppCompatActivity {
//
//    private static final String TAG = "RegisterActivity";
//
//    // Step constants
//    private static final int STEP_NAME = 0;
//    private static final int STEP_BIRTH = 1;
//    private static final int STEP_USERNAME = 2;
//    private static final int STEP_PASSWORD = 3;
//    private static final int STEP_PHONE = 4;
//    private static final int STEP_PROFILE = 5;
//    private static final int TOTAL_STEPS = 6;
//
//    private static final int REQUEST_IMG = 1001;
//    private static final int REQUEST_STORAGE_PERMISSION = 1002;
//    // Current step
//    private int currentStep = 0;
//
//    // Views for sliding animation
//    private View[] stepViews = new View[TOTAL_STEPS];
//    private View containerView;
//
//    // Step 1: Name
//    private EditText etFirstName, etLastName;
//
//    // Step 2: Birth & Gender
//    private EditText etBirthDate;
//    private RadioGroup rgGender;
//    private RadioButton rbFemale, rbMale, rbOther;
//
//    // Step 3: Username
//    private EditText etUsername;
//    private TextView tvUsernameError;
//
//    // Step 4: Password
//    private EditText etPassword, etConfirmPassword;
//    private CheckBox cbShowPassword;
//
//    // Step 5: Phone
//    private EditText etPhone;
//
//    // Step 6: Profile (Optional)
//    private ImageView imgProfile;
//    private Button btnSelectImage;
//    private TextView tvProfileHint;
//    private Uri profileUri;
//
//    // Navigation buttons
//    private Button btnNext, btnBack;
//    private TextView tvStepTitle;
//
//    // API
//    private AuthService apiService;
//
//    // Data storage
//    private String firstName, lastName, birthDate, gender, username, password, phone;
//    private String profilePic;
//    // Flag to track if username is available
//    private boolean isUsernameAvailable = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        initViews();
//        setupApiService();
//        setupClickListeners();
//        updateStepVisibility();
//    }
//
//    private void initViews() {
//        containerView = findViewById(R.id.containerView);
//
//        // Step views
//        stepViews[STEP_NAME] = findViewById(R.id.stepName);
//        stepViews[STEP_BIRTH] = findViewById(R.id.stepBirth);
//        stepViews[STEP_USERNAME] = findViewById(R.id.stepUsername);
//        stepViews[STEP_PASSWORD] = findViewById(R.id.stepPassword);
//        stepViews[STEP_PHONE] = findViewById(R.id.stepPhone);
//        stepViews[STEP_PROFILE] = findViewById(R.id.stepProfile);
//
//        // Navigation
//        btnNext = findViewById(R.id.btnNext);
//        btnBack = findViewById(R.id.btnBack);
//        tvStepTitle = findViewById(R.id.tvStepTitle);
//
//        // Step 1: Name
//        etFirstName = findViewById(R.id.etFirstName);
//        etLastName = findViewById(R.id.etLastName);
//
//        // Step 2: Birth & Gender
//        etBirthDate = findViewById(R.id.etBirthDate);
//        rgGender = findViewById(R.id.rgGender);
//        rbFemale = findViewById(R.id.rbFemale);
//        rbMale = findViewById(R.id.rbMale);
//        rbOther = findViewById(R.id.rbOther);
//
//        // Step 3: Username
//        etUsername = findViewById(R.id.etUsername);
//        tvUsernameError = findViewById(R.id.tvUsernameError);
//
//        // Step 4: Password
//        etPassword = findViewById(R.id.etPassword);
//        etConfirmPassword = findViewById(R.id.etConfirmPassword);
//        cbShowPassword = findViewById(R.id.cbShowPassword);
//
//        // Step 5: Phone
//        etPhone = findViewById(R.id.etPhone);
//
//        // Step 6: Profile
//        imgProfile = findViewById(R.id.imgProfile);
//        btnSelectImage = findViewById(R.id.btnSelectImage);
//        tvProfileHint = findViewById(R.id.tvProfileHint);
//    }
//
//    private void setupApiService() {
//        // pass *this* (Activity) or getApplicationContext() into getClient(...)
//        apiService = ApiClient
//                .getClient(this)               // ← here!
//                .create(AuthService.class);
//    }
//
//    private void setupClickListeners() {
//        btnNext.setOnClickListener(v -> handleNext());
//        btnBack.setOnClickListener(v -> handleBack());
//
//        // Birth date picker
//        etBirthDate.setOnClickListener(v -> showDatePicker());
//
//        // Show/hide password
//        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            int inputType = isChecked ?
//                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
//                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
//            etPassword.setInputType(inputType);
//            etConfirmPassword.setInputType(inputType);
//        });
//
//        // Profile image selection
//        btnSelectImage.setOnClickListener(v -> selectImage());
//        imgProfile.setOnClickListener(v -> selectImage());
//    }
//
//    private void showDatePicker() {
//        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
//                this,
//                (view, year, month, dayOfMonth) -> {
//                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
//                    etBirthDate.setText(date);
//                },
//                2000, 0, 1
//        );
//        datePickerDialog.show();
//    }
//
//private void selectImage() {
//    // Check if we need permission (for Android 6.0+)
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    REQUEST_STORAGE_PERMISSION);
//            return;
//        }
//    }
//
//    openFileChooser();
//}
//
//    private void openFileChooser() {
//        // Use ACTION_GET_CONTENT for better file system access
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        // Allow multiple MIME types for better compatibility
//        String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//
//        // Enable local files only (no cloud storage)
//        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//
//        // Create chooser intent
//        Intent chooserIntent = Intent.createChooser(intent, "Select Image");
//
//        // Add alternative intent for file manager access
//        Intent fileManagerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        fileManagerIntent.setType("image/*");
//        fileManagerIntent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        // Add alternative intents array
//        Intent[] intentArray = {fileManagerIntent};
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
//
//        try {
//            startActivityForResult(chooserIntent, REQUEST_IMG);
//        } catch (Exception e) {
//            Log.e(TAG, "Error opening file chooser", e);
//            Toast.makeText(this, "Unable to open file chooser", Toast.LENGTH_SHORT).show();
//
//            // Fallback to basic gallery intent
//            try {
//                Intent fallbackIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(fallbackIntent, REQUEST_IMG);
//            } catch (Exception ex) {
//                Toast.makeText(this, "Unable to access files", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // Add permission request handler
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == REQUEST_STORAGE_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openFileChooser();
//            } else {
//                Toast.makeText(this, "Permission denied to access storage", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // Update the onActivityResult method
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_IMG && resultCode == RESULT_OK) {
//            if (data != null && data.getData() != null) {
//                profileUri = data.getData();
//
//                // Validate the selected file
//                if (isValidImageFile(profileUri)) {
//                    imgProfile.setImageURI(profileUri);
//                    imgProfile.setVisibility(View.VISIBLE);
//                    tvProfileHint.setText("Image selected: " + getFileName(profileUri));
//                } else {
//                    Toast.makeText(this, "Please select a valid image file", Toast.LENGTH_SHORT).show();
//                    profileUri = null;
//                }
//            } else {
//                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // Helper method to validate image file
//    private boolean isValidImageFile(Uri uri) {
//        try {
//            String mimeType = getContentResolver().getType(uri);
//            if (mimeType != null && mimeType.startsWith("image/")) {
//                return true;
//            }
//
//            // Fallback: check file extension
//            String fileName = getFileName(uri);
//            if (fileName != null) {
//                String extension = fileName.toLowerCase();
//                return extension.endsWith(".jpg") || extension.endsWith(".jpeg") ||
//                        extension.endsWith(".png") || extension.endsWith(".gif") ||
//                        extension.endsWith(".webp");
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error validating image file", e);
//        }
//        return false;
//    }
//
//    // Helper method to get file name from URI
//    private String getFileName(Uri uri) {
//        return FileUtils.getFileName(this, uri);
//    }
//private void handleNext() {
//    if (currentStep == STEP_USERNAME) {
//        checkUsernameAvailabilityAndProceed();  // only proceed inside callback
//        return;
//    }
//
//    if (validateCurrentStep()) {
//        if (currentStep < TOTAL_STEPS - 1) {
//            currentStep++;
//            animateToStep();
//        } else {
//            attemptRegister();
//        }
//    }
//}
//
//    private void handleBack() {
//        if (currentStep > 0) {
//            currentStep--;
//            animateToStep();
//        } else {
//            // If we're on the first step, go back to LoginActivity
//            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
//
////    private void animateToStep() {
////        containerView.post(() -> {
////            updateStepVisibility(); // still before animating
////
////            int stepWidth = containerView.getWidth();
////            float translationX = -currentStep * stepWidth;
////
////            ObjectAnimator animator = ObjectAnimator.ofFloat(containerView, "translationX", translationX);
////            animator.setDuration(300);
////            animator.setInterpolator(new DecelerateInterpolator());
////            animator.start();
////        });
////    }
//
//    private void animateToStep() {
//        updateStepVisibility();
//    }
//
//
////    private void animateToStep() {
////        // How wide each “page”/step is
////        int stepWidth = containerView.getWidth();
////        // Compute the target X offset
////        float targetX = -currentStep * stepWidth;
////
////        // Animate sliding the container
////        containerView.animate()
////                .translationX(targetX)
////                .setDuration(300L)
////                .setInterpolator(new DecelerateInterpolator())
////                .withEndAction(() -> {
////                    // Once the slide is done, update which views are visible
////                    updateStepVisibility();
////                })
////                .start();
////    }
//
//
//private void updateStepVisibility() {
//    // Update step title
//    String[] stepTitles = {
//            "Enter Your Name",
//            "Birth Date & Gender",
//            "Choose Username",
//            "Create Password",
//            "Phone Number",
//            "Profile Picture (Optional)"
//    };
//
//
//    tvStepTitle.setText(stepTitles[currentStep]);
//
//    // Update button text
//    btnNext.setText(currentStep == TOTAL_STEPS - 1 ? "Sign Up" : "Next");
//
//    // Always show back button, but change text for first step
//    btnBack.setVisibility(View.VISIBLE);
//    btnBack.setText(currentStep == 0 ? "Back to Login" : "Back");
//
//    // Show/hide step views
//    for (int i = 0; i < TOTAL_STEPS; i++) {
//        stepViews[i].setVisibility(i == currentStep ? View.VISIBLE : View.GONE);
//    }
//
//    // Reset translation to ensure container stays in view
//    containerView.setTranslationX(0);
//
//    Log.d("RegisterActivity", "Step " + currentStep + " visibility: " + stepViews[currentStep].getVisibility());
//}
//    private boolean validateCurrentStep() {
//        switch (currentStep) {
//            case STEP_NAME:
//                return validateName();
//            case STEP_BIRTH:
//                return validateBirthAndGender();
//            case STEP_USERNAME:
//                return validateUsername();
//            case STEP_PASSWORD:
//                return validatePassword();
//            case STEP_PHONE:
//                return validatePhone();
//            case STEP_PROFILE:
//                return validateProfile();
//            default:
//                return false;
//        }
//    }
//
//    private boolean validateName() {
//        firstName = etFirstName.getText().toString().trim();
//        lastName = etLastName.getText().toString().trim();
//
//        if (TextUtils.isEmpty(firstName)) {
//            etFirstName.setError("First name is required");
//            etFirstName.requestFocus();
//            return false;
//        }
//
//        if (!Pattern.matches("^[A-Za-z]+$", firstName)) {
//            etFirstName.setError("First name must contain only English letters");
//            etFirstName.requestFocus();
//            return false;
//        }
//
//        if (TextUtils.isEmpty(lastName)) {
//            etLastName.setError("Last name is required");
//            etLastName.requestFocus();
//            return false;
//        }
//
//        if (!Pattern.matches("^[A-Za-z]+$", lastName)) {
//            etLastName.setError("Last name must contain only English letters");
//            etLastName.requestFocus();
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean validateBirthAndGender() {
//        birthDate = etBirthDate.getText().toString().trim();
//
//        if (TextUtils.isEmpty(birthDate)) {
//            etBirthDate.setError("Birth date is required");
//            etBirthDate.requestFocus();
//            return false;
//        }
//
//        // Check if birth date is in the future
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            Date birth = sdf.parse(birthDate);
//            if (birth != null && birth.after(new Date())) {
//                etBirthDate.setError("Birth date must be in the past");
//                etBirthDate.requestFocus();
//                return false;
//            }
//        } catch (Exception e) {
//            etBirthDate.setError("Invalid date format");
//            etBirthDate.requestFocus();
//            return false;
//        }
//
//        int selectedGenderId = rgGender.getCheckedRadioButtonId();
//        if (selectedGenderId == -1) {
//            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        RadioButton selectedGender = findViewById(selectedGenderId);
//        gender = selectedGender.getText().toString().toLowerCase();
//
//        return true;
//    }
//    private boolean validateUsername() {
//        username = etUsername.getText().toString().trim();
//
//        if (TextUtils.isEmpty(username)) {
//            etUsername.setError("Username is required");
//            etUsername.requestFocus();
//            return false;
//        }
//
//        if (username.length() < 3) {
//            etUsername.setError("Username must be at least 3 characters");
//            etUsername.requestFocus();
//            return false;
//        }
//
//        if (!Pattern.matches("^[a-zA-Z0-9]+$", username)) {
//            etUsername.setError("Username must contain only English letters and numbers");
//            etUsername.requestFocus();
//            return false;
//        }
//
//        return true;
//    }
//
//private void checkUsernameAvailabilityAndProceed() {
//    username = etUsername.getText().toString().trim();
//
//    if (TextUtils.isEmpty(username)) {
//        etUsername.setError("Username is required");
//        etUsername.requestFocus();
//        return;
//    }
//
//    if (username.length() < 3) {
//        etUsername.setError("Username must be at least 3 characters");
//        etUsername.requestFocus();
//        return;
//    }
//
//    if (!Pattern.matches("^[a-zA-Z0-9]+$", username)) {
//        etUsername.setError("Username must contain only English letters and numbers");
//        etUsername.requestFocus();
//        return;
//    }
//
//    btnNext.setEnabled(false);
//    tvUsernameError.setVisibility(View.GONE);
//
//    Call<UsernameCheckResponse> call = apiService.checkUsername(username);
//    call.enqueue(new Callback<UsernameCheckResponse>() {
//        @Override
//        public void onResponse(Call<UsernameCheckResponse> call, Response<UsernameCheckResponse> response) {
//            btnNext.setEnabled(true);
//
//            if (response.isSuccessful() && response.body() != null) {
//                if (response.body().isExists()) {
//                    isUsernameAvailable = false;
//                    etUsername.setError("Username already exists. Please choose a different one");
//                    tvUsernameError.setText("Username already exists. Please choose a different one");
//                    tvUsernameError.setVisibility(View.VISIBLE);
//                } else {
//                    isUsernameAvailable = true;
//                    tvUsernameError.setVisibility(View.GONE);
//                    currentStep++;
//                    animateToStep();
//                }
//            } else {
//                isUsernameAvailable = false;
//                tvUsernameError.setText("Failed to validate username. Please try again.");
//                tvUsernameError.setVisibility(View.VISIBLE);
//            }
//        }
//
//        @Override
//        public void onFailure(Call<UsernameCheckResponse> call, Throwable t) {
//            btnNext.setEnabled(true);
//            isUsernameAvailable = false;
//            tvUsernameError.setText("Network error. Please try again.");
//            tvUsernameError.setVisibility(View.VISIBLE);
//            Log.e(TAG, "Username validation failed", t);
//        }
//    });
//}
//
//    private boolean validatePassword() {
//        password = etPassword.getText().toString();
//        String confirmPassword = etConfirmPassword.getText().toString();
//
//        if (TextUtils.isEmpty(password)) {
//            etPassword.setError("Password is required");
//            etPassword.requestFocus();
//            return false;
//        }
//
//        if (password.length() < 6) {
//            etPassword.setError("Password must be at least 6 characters");
//            etPassword.requestFocus();
//            return false;
//        }
//
//        if (password.contains(" ")) {
//            etPassword.setError("Password cannot contain spaces");
//            etPassword.requestFocus();
//            return false;
//        }
//
//        if (TextUtils.isEmpty(confirmPassword)) {
//            etConfirmPassword.setError("Please confirm your password");
//            etConfirmPassword.requestFocus();
//            return false;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            etConfirmPassword.setError("Passwords must match");
//            etConfirmPassword.requestFocus();
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean validatePhone() {
//        phone = etPhone.getText().toString().trim();
//
//        if (TextUtils.isEmpty(phone)) {
//            etPhone.setError("Phone number is required");
//            etPhone.requestFocus();
//            return false;
//        }
//
//        if (!Pattern.matches("^05\\d{8}$", phone)) {
//            etPhone.setError("Phone number must be 10 digits long and start with '05'");
//            etPhone.requestFocus();
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean validateProfile() {
//        // Profile picture is optional, so always return true
//        // But validate file type if selected
//        if (profileUri != null) {
//            String fileName = FileUtils.getFileName(this, profileUri);
//            if (fileName != null && !fileName.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
//                Toast.makeText(this, "Please upload a valid image file (jpg, jpeg, png, gif)", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        }
//        return true;
//    }
//    private void attemptRegister() {
//        // Check username availability one more time before proceeding
//        if (!isUsernameAvailable) {
//            Toast.makeText(this, "Please choose a different username", Toast.LENGTH_SHORT).show();
//            currentStep = STEP_USERNAME;
//            updateStepVisibility();
//            return;
//        }
//
//        // Collect all form data
//        String firstName = etFirstName.getText().toString().trim();
//        String lastName = etLastName.getText().toString().trim();
//        String username = etUsername.getText().toString().trim();
//        String password = etPassword.getText().toString();
//        String phone = etPhone.getText().toString().trim();
//        String birthDate = etBirthDate.getText().toString().trim();
//        String gender = (rgGender.getCheckedRadioButtonId() == R.id.rbFemale)
//                ? "female"
//                : (rgGender.getCheckedRadioButtonId() == R.id.rbMale)
//                ? "male"
//                : "other";
//
//        // Create text parts for multipart request
//        Map<String, RequestBody> data = new HashMap<>();
//        data.put("firstName", RequestBody.create(MediaType.parse("text/plain"), firstName));
//        data.put("lastName", RequestBody.create(MediaType.parse("text/plain"), lastName));
//        data.put("username", RequestBody.create(MediaType.parse("text/plain"), username));
//        data.put("password", RequestBody.create(MediaType.parse("text/plain"), password));
//        data.put("phoneNumber", RequestBody.create(MediaType.parse("text/plain"), phone));
//        data.put("birthDate", RequestBody.create(MediaType.parse("text/plain"), birthDate));
//        data.put("gender", RequestBody.create(MediaType.parse("text/plain"), gender));
//
//        // Handle profile picture if selected
//        MultipartBody.Part profilePicPart = null;
//        if (profileUri != null) {
//            try {
//                String filePath = FileUtils.getPath(this, profileUri);
//                if (filePath != null) {
//                    File file = new File(filePath);
//                    String mimeType = getContentResolver().getType(profileUri);
//                    if (mimeType == null) {
//                        mimeType = "image/jpeg"; // default
//                    }
//
//                    RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
//                    profilePicPart = MultipartBody.Part.createFormData("profilePic", file.getName(), requestFile);
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "Error preparing image for upload", e);
//                Toast.makeText(this, "Error preparing image for upload", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//        // Show loading indicator
//        btnNext.setEnabled(false);
//        btnNext.setText("Registering...");
//
//        // Send registration request
//        Call<RegisterResponse> call = apiService.register(data, profilePicPart);
//        call.enqueue(new Callback<RegisterResponse>() {
//            @Override
//            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
//                btnNext.setEnabled(true);
//                btnNext.setText("Sign Up");
//
//                if (response.isSuccessful() && response.body() != null) {
//                    String message = response.body().getMessage();
//                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
//
//                    // Navigate to login activity
//                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    String errorMessage = "Registration failed";
//                    try {
//                        if (response.errorBody() != null) {
//                            errorMessage = response.errorBody().string();
//                        }
//                    } catch (IOException e) {
//                        Log.e(TAG, "Error reading error body", e);
//                    }
//                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RegisterResponse> call, Throwable t) {
//                btnNext.setEnabled(true);
//                btnNext.setText("Sign Up");
//
//                Log.e(TAG, "Registration request failed", t);
//                Toast.makeText(RegisterActivity.this,
//                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//}

package com.example.myemailapp.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.CheckBox;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.view.animation.DecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.myemailapp.viewmodel.RegisterViewModel;
import com.example.myemailapp.R;
import com.example.myemailapp.FileUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.Manifest;
import android.content.pm.PackageManager;

import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int REQUEST_IMG = 1001;
    private static final int REQUEST_STORAGE_PERMISSION = 1002;

    // ViewModel
    private RegisterViewModel viewModel;

    // Views for sliding animation
    private View[] stepViews = new View[RegisterViewModel.TOTAL_STEPS];
    private View containerView;

    // Step 1: Name
    private EditText etFirstName, etLastName;

    // Step 2: Birth & Gender
    private EditText etBirthDate;
    private RadioGroup rgGender;
    private RadioButton rbFemale, rbMale, rbOther;

    // Step 3: Username
    private EditText etUsername;
    private TextView tvUsernameError;

    // Step 4: Password
    private EditText etPassword, etConfirmPassword;
    private CheckBox cbShowPassword;

    // Step 5: Phone
    private EditText etPhone;

    // Step 6: Profile (Optional)
    private ImageView imgProfile;
    private Button btnSelectImage;
    private TextView tvProfileHint;

    // Navigation buttons
    private Button btnNext, btnBack;
    private TextView tvStepTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        containerView = findViewById(R.id.containerView);

        // Step views
        stepViews[RegisterViewModel.STEP_NAME] = findViewById(R.id.stepName);
        stepViews[RegisterViewModel.STEP_BIRTH] = findViewById(R.id.stepBirth);
        stepViews[RegisterViewModel.STEP_USERNAME] = findViewById(R.id.stepUsername);
        stepViews[RegisterViewModel.STEP_PASSWORD] = findViewById(R.id.stepPassword);
        stepViews[RegisterViewModel.STEP_PHONE] = findViewById(R.id.stepPhone);
        stepViews[RegisterViewModel.STEP_PROFILE] = findViewById(R.id.stepProfile);

        // Navigation
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        tvStepTitle = findViewById(R.id.tvStepTitle);

        // Step 1: Name
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);

        // Step 2: Birth & Gender
        etBirthDate = findViewById(R.id.etBirthDate);
        rgGender = findViewById(R.id.rgGender);
        rbFemale = findViewById(R.id.rbFemale);
        rbMale = findViewById(R.id.rbMale);
        rbOther = findViewById(R.id.rbOther);

        // Step 3: Username
        etUsername = findViewById(R.id.etUsername);
        tvUsernameError = findViewById(R.id.tvUsernameError);

        // Step 4: Password
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbShowPassword = findViewById(R.id.cbShowPassword);

        // Step 5: Phone
        etPhone = findViewById(R.id.etPhone);

        // Step 6: Profile
        imgProfile = findViewById(R.id.imgProfile);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        tvProfileHint = findViewById(R.id.tvProfileHint);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    private void setupClickListeners() {
        btnNext.setOnClickListener(v -> handleNext());
        btnBack.setOnClickListener(v -> handleBack());

        // Birth date picker
        etBirthDate.setOnClickListener(v -> showDatePicker());

        // Show/hide password
        cbShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int inputType = isChecked ?
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
            etPassword.setInputType(inputType);
            etConfirmPassword.setInputType(inputType);
        });

        // Profile image selection
        btnSelectImage.setOnClickListener(v -> selectImage());
        imgProfile.setOnClickListener(v -> selectImage());
    }

    private void observeViewModel() {
        // Observe current step
        viewModel.getCurrentStep().observe(this, step -> {
            if (step != null) {
                updateStepVisibility(step);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                btnNext.setEnabled(!isLoading);
                updateNextButtonText();
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, error -> {
            if (!TextUtils.isEmpty(error)) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                showFieldError(error);
            }
        });

        // Observe success messages
        viewModel.getSuccessMessage().observe(this, success -> {
            if (!TextUtils.isEmpty(success)) {
                Toast.makeText(this, success, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe username availability
        viewModel.getIsUsernameAvailable().observe(this, isAvailable -> {
            if (isAvailable != null && isAvailable) {
                viewModel.nextStep();
            }
        });

        // Observe username error
        viewModel.getUsernameError().observe(this, error -> {
            if (!TextUtils.isEmpty(error)) {
                etUsername.setError(error);
                tvUsernameError.setText(error);
                tvUsernameError.setVisibility(View.VISIBLE);
            } else {
                tvUsernameError.setVisibility(View.GONE);
            }
        });

        // Observe registration success
        viewModel.getRegistrationSuccess().observe(this, success -> {
            if (success != null && success) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Observe profile image
        viewModel.getProfileImageUri().observe(this, uri -> {
            if (uri != null) {
                imgProfile.setImageURI(uri);
                imgProfile.setVisibility(View.VISIBLE);
            }
        });

        // Observe profile image name
        viewModel.getProfileImageName().observe(this, name -> {
            if (!TextUtils.isEmpty(name)) {
                tvProfileHint.setText("Image selected: " + name);
            } else {
                tvProfileHint.setText("No image selected");
            }
        });
    }

    private void showDatePicker() {
        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etBirthDate.setText(date);
                },
                2000, 0, 1
        );
        datePickerDialog.show();
    }

    private void selectImage() {
        // Check if we need permission (for Android 6.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
                return;
            }
        }

        openFileChooser();
    }

    private void openFileChooser() {
        // Use ACTION_GET_CONTENT for better file system access
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Allow multiple MIME types for better compatibility
        String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Enable local files only (no cloud storage)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // Create chooser intent
        Intent chooserIntent = Intent.createChooser(intent, "Select Image");

        // Add alternative intent for file manager access
        Intent fileManagerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        fileManagerIntent.setType("image/*");
        fileManagerIntent.addCategory(Intent.CATEGORY_OPENABLE);

        // Add alternative intents array
        Intent[] intentArray = {fileManagerIntent};
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        try {
            startActivityForResult(chooserIntent, REQUEST_IMG);
        } catch (Exception e) {
            Log.e(TAG, "Error opening file chooser", e);
            Toast.makeText(this, "Unable to open file chooser", Toast.LENGTH_SHORT).show();

            // Fallback to basic gallery intent
            try {
                Intent fallbackIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(fallbackIntent, REQUEST_IMG);
            } catch (Exception ex) {
                Toast.makeText(this, "Unable to access files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Permission denied to access storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMG && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri profileUri = data.getData();

                // Validate the selected file
                if (viewModel.isValidImageFile(profileUri)) {
                    viewModel.setProfileImage(profileUri);
                } else {
                    Toast.makeText(this, "Please select a valid image file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleNext() {
        Integer currentStep = viewModel.getCurrentStep().getValue();
        if (currentStep == null) return;

        viewModel.clearMessages();

        if (currentStep == RegisterViewModel.STEP_USERNAME) {
            String username = etUsername.getText().toString().trim();
            viewModel.checkUsernameAvailability(username);
            return;
        }

        if (validateCurrentStep(currentStep)) {
            if (currentStep < RegisterViewModel.TOTAL_STEPS - 1) {
                viewModel.nextStep();
            } else {
                // Set all form data in ViewModel before registering
                setFormDataInViewModel();
                viewModel.attemptRegister();
            }
        }
    }

    private void setFormDataInViewModel() {
        viewModel.setFirstName(etFirstName.getText().toString().trim());
        viewModel.setLastName(etLastName.getText().toString().trim());
        viewModel.setBirthDate(etBirthDate.getText().toString().trim());

        // Get selected gender
        String selectedGender = "";
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId != -1) {
            RadioButton selectedGenderButton = findViewById(selectedGenderId);
            selectedGender = selectedGenderButton.getText().toString().toLowerCase();
        }
        viewModel.setGender(selectedGender);

        viewModel.setUsername(etUsername.getText().toString().trim());
        viewModel.setPassword(etPassword.getText().toString());
        viewModel.setPhone(etPhone.getText().toString().trim());
    }

    private void handleBack() {
        Integer currentStep = viewModel.getCurrentStep().getValue();
        if (currentStep == null) return;

        if (currentStep > 0) {
            viewModel.previousStep();
        } else {
            // If we're on the first step, go back to LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateCurrentStep(int currentStep) {
        switch (currentStep) {
            case RegisterViewModel.STEP_NAME:
                return viewModel.validateName(
                        etFirstName.getText().toString(),
                        etLastName.getText().toString()
                );
            case RegisterViewModel.STEP_BIRTH:
                String selectedGender = "";
                int selectedGenderId = rgGender.getCheckedRadioButtonId();
                if (selectedGenderId != -1) {
                    RadioButton selectedGenderButton = findViewById(selectedGenderId);
                    selectedGender = selectedGenderButton.getText().toString().toLowerCase();
                }
                return viewModel.validateBirthAndGender(
                        etBirthDate.getText().toString(),
                        selectedGender
                );
            case RegisterViewModel.STEP_USERNAME:
                return viewModel.validateUsername(etUsername.getText().toString());
            case RegisterViewModel.STEP_PASSWORD:
                return viewModel.validatePassword(
                        etPassword.getText().toString(),
                        etConfirmPassword.getText().toString()
                );
            case RegisterViewModel.STEP_PHONE:
                return viewModel.validatePhone(etPhone.getText().toString());
            case RegisterViewModel.STEP_PROFILE:
                Uri profileUri = viewModel.getProfileImageUri().getValue();
                return viewModel.validateProfile(profileUri);
            default:
                return false;
        }
    }

    private void updateStepVisibility(int currentStep) {
        // Update step title
        String[] stepTitles = {
                "Enter Your Name",
                "Birth Date & Gender",
                "Choose Username",
                "Create Password",
                "Phone Number",
                "Profile Picture (Optional)"
        };

        tvStepTitle.setText(stepTitles[currentStep]);
        updateNextButtonText();

        // Always show back button, but change text for first step
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setText(currentStep == 0 ? "Back to Login" : "Back");

        // Show/hide step views
        for (int i = 0; i < RegisterViewModel.TOTAL_STEPS; i++) {
            stepViews[i].setVisibility(i == currentStep ? View.VISIBLE : View.GONE);
        }

        // Reset translation to ensure container stays in view
        containerView.setTranslationX(0);

        Log.d("RegisterActivity", "Step " + currentStep + " visibility: " + stepViews[currentStep].getVisibility());
    }

    private void updateNextButtonText() {
        Integer currentStep = viewModel.getCurrentStep().getValue();
        Boolean isLoading = viewModel.getIsLoading().getValue();

        if (currentStep == null) return;

        if (isLoading != null && isLoading) {
            if (currentStep == RegisterViewModel.STEP_USERNAME) {
                btnNext.setText("Checking...");
            } else if (currentStep == RegisterViewModel.TOTAL_STEPS - 1) {
                btnNext.setText("Registering...");
            } else {
                btnNext.setText("Loading...");
            }
        } else {
            btnNext.setText(currentStep == RegisterViewModel.TOTAL_STEPS - 1 ? "Sign Up" : "Next");
        }
    }

    private void showFieldError(String error) {
        Integer currentStep = viewModel.getCurrentStep().getValue();
        if (currentStep == null) return;

        switch (currentStep) {
            case RegisterViewModel.STEP_NAME:
                if (error.toLowerCase().contains("first name")) {
                    etFirstName.setError(error);
                    etFirstName.requestFocus();
                } else if (error.toLowerCase().contains("last name")) {
                    etLastName.setError(error);
                    etLastName.requestFocus();
                }
                break;
            case RegisterViewModel.STEP_BIRTH:
                if (error.toLowerCase().contains("birth") || error.toLowerCase().contains("date")) {
                    etBirthDate.setError(error);
                    etBirthDate.requestFocus();
                }
                break;
            case RegisterViewModel.STEP_USERNAME:
                etUsername.setError(error);
                etUsername.requestFocus();
                break;
            case RegisterViewModel.STEP_PASSWORD:
                if (error.toLowerCase().contains("confirm") || error.toLowerCase().contains("match")) {
                    etConfirmPassword.setError(error);
                    etConfirmPassword.requestFocus();
                } else {
                    etPassword.setError(error);
                    etPassword.requestFocus();
                }
                break;
            case RegisterViewModel.STEP_PHONE:
                etPhone.setError(error);
                etPhone.requestFocus();
                break;
        }
    }
}