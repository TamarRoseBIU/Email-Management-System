package com.example.myemailapp.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myemailapp.data.database.entity.Label;
import com.example.myemailapp.repository.LabelRepository;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;

public class LabelViewModel extends AndroidViewModel {
    private static final String TAG = "LabelViewModel";
    private static final long REFRESH_INTERVAL = 3000; // 5 seconds

    private LabelRepository labelRepository;
    private Handler handler;
    private Runnable refreshRunnable;
    private boolean isPeriodicRefreshActive = false;

    // Add fallback error handling
    private final MutableLiveData<String> initializationError = new MutableLiveData<>();

    public LabelViewModel(@NonNull Application application) {
        super(application);

        try {
            // Get auth token
            SharedPreferences prefs = application.getSharedPreferences("auth", MODE_PRIVATE);
            String authToken = prefs.getString("jwt", "");

            // Initialize repository
            labelRepository = LabelRepository.getInstance(application, authToken);

            // Initialize handler for periodic refresh
            handler = new Handler(Looper.getMainLooper());

            Log.d(TAG, "LabelViewModel initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing LabelViewModel", e);
            initializationError.setValue("Failed to initialize: " + e.getMessage());
        }
    }

    /**
     * Get labels as List<String> - maintains compatibility with existing code
     */
    public LiveData<List<String>> getLabels() {
        if (labelRepository != null) {
            return labelRepository.getLabels();
        } else {
            return new MutableLiveData<>();
        }
    }

    /**
     * Get labels as Label entities - for when you need full Label objects
     */
    public LiveData<List<Label>> getLabelEntities() {
        if (labelRepository != null) {
            return labelRepository.getLabelEntities();
        } else {
            return new MutableLiveData<>();
        }
    }

    public LiveData<String> getErrorMessage() {
        if (labelRepository != null) {
            return labelRepository.getError();
        } else {
            return initializationError;
        }
    }

    public LiveData<Boolean> getIsLoading() {
        if (labelRepository != null) {
            return labelRepository.getIsLoading();
        } else {
            MutableLiveData<Boolean> fallback = new MutableLiveData<>();
            fallback.setValue(false);
            return fallback;
        }
    }

    public void loadLabels() {
        if (labelRepository != null) {
            labelRepository.loadLabels();
        } else {
            Log.e(TAG, "Cannot load labels - repository not initialized");
        }
    }

    public void refreshLabels() {
        if (labelRepository != null) {
            labelRepository.refreshLabels();
        } else {
            Log.e(TAG, "Cannot refresh labels - repository not initialized");
        }
    }

    /**
     * Add a new label
     */
    public void addLabel(Label label) {
        if (labelRepository != null) {
            labelRepository.addLabel(label);
        } else {
            Log.e(TAG, "Cannot add label - repository not initialized");
        }
    }

    /**
     * Update an existing label
     */
    public void updateLabel(String labelId, String newName) {
        if (labelRepository != null) {
            labelRepository.updateLabel(labelId, newName);
        } else {
            Log.e(TAG, "Cannot update label - repository not initialized");
        }
    }

    /**
     * Delete a label
     */
    public void deleteLabel(String labelId) {
        if (labelRepository != null) {
            labelRepository.deleteLabel(labelId);
        } else {
            Log.e(TAG, "Cannot delete label - repository not initialized");
        }
    }

    /**
     * Start periodic refresh of labels
     */
    public void startPeriodicRefresh() {
        if (labelRepository == null) {
            Log.e(TAG, "Cannot start periodic refresh - repository not initialized");
            return;
        }

        if (isPeriodicRefreshActive) {
            return; // Already running
        }

        isPeriodicRefreshActive = true;
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPeriodicRefreshActive && labelRepository != null) {
                    labelRepository.refreshLabels();
                    handler.postDelayed(this, REFRESH_INTERVAL);
                }
            }
        };

        // Start the periodic refresh
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    /**
     * Stop periodic refresh of labels
     */
    public void stopPeriodicRefresh() {
        isPeriodicRefreshActive = false;
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }

    /**
     * Set custom refresh interval (in milliseconds)
     */
    public void setRefreshInterval(long intervalMs) {
        stopPeriodicRefresh();
        // You could store this in a field if you want to make it configurable
        startPeriodicRefresh();
    }

    /**
     * Check if periodic refresh is currently active
     */
    public boolean isPeriodicRefreshActive() {
        return isPeriodicRefreshActive;
    }

    /**
     * Clear all labels from local database
     */
    public void clearLabels() {
        if (labelRepository != null) {
            labelRepository.clearLabels();
        } else {
            Log.e(TAG, "Cannot clear labels - repository not initialized");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Stop periodic refresh when ViewModel is destroyed
        stopPeriodicRefresh();
    }
}