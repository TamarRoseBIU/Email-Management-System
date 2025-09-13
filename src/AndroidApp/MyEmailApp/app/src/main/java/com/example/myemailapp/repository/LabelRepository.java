package com.example.myemailapp.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.myemailapp.data.database.entity.Label;
import com.example.myemailapp.data.database.network.LabelApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LabelRepository {
    private static final String TAG = "LabelRepository";
    private static LabelRepository instance;

    private final LabelApiService apiService;
    private final ExecutorService executor;

    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();

    // Store labels in memory instead of database
    private final MutableLiveData<List<Label>> labelsLiveData = new MutableLiveData<>();

    private LabelRepository(Context context, String token) {
        try {
            apiService = new LabelApiService(context, token);
            executor = Executors.newFixedThreadPool(4);

            // Initialize with empty list
            labelsLiveData.setValue(new ArrayList<>());

            Log.d(TAG, "LabelRepository initialized successfully (no database)");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing LabelRepository", e);
            throw new RuntimeException("Failed to initialize LabelRepository", e);
        }
    }

    public static synchronized LabelRepository getInstance(Context context, String token) {
        if (instance == null) {
            instance = new LabelRepository(context, token);
        }
        return instance;
    }

    /**
     * Returns LiveData<List<String>> to maintain compatibility with existing code
     */
    public LiveData<List<String>> getLabels() {
        return Transformations.map(labelsLiveData, labels -> {
            if (labels != null) {
                List<String> labelNames = new ArrayList<>();
                for (Label label : labels) {
                    labelNames.add(label.getName());
                }
                return labelNames;
            }
            return new ArrayList<>();
        });
    }

    /**
     * Returns LiveData<List<Label>> for when you need full Label objects
     */
    public LiveData<List<Label>> getLabelEntities() {
        return labelsLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public void loadLabels() {
        // Since we don't have a database, always fetch from API
        fetchFromApi();
    }

    public void refreshLabels() {
        fetchFromApi();
    }

    /**
     * Add a label via API call
     */
    public void addLabel(Label label) {
        isLoadingLiveData.postValue(true);

        apiService.createLabel(label, new LabelApiService.ApiCallback<Label>() {
            @Override
            public void onSuccess(Label createdLabel) {
                // Add to memory after successful API call
                List<Label> currentLabels = labelsLiveData.getValue();
                if (currentLabels != null) {
                    List<Label> updatedLabels = new ArrayList<>(currentLabels);
                    updatedLabels.add(createdLabel);
                    labelsLiveData.postValue(updatedLabels);
                }
                isLoadingLiveData.postValue(false);
                Log.d(TAG, "Label added successfully: " + createdLabel.getName());
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue("Failed to add label: " + error);
                isLoadingLiveData.postValue(false);
                Log.e(TAG, "Error adding label: " + error);
            }
        });
    }

    /**
     * Delete a label via API call
     */
    public void deleteLabel(String labelId) {
        isLoadingLiveData.postValue(true);

        apiService.deleteLabel(labelId, new LabelApiService.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Remove from memory after successful API call
                List<Label> currentLabels = labelsLiveData.getValue();
                if (currentLabels != null) {
                    List<Label> updatedLabels = new ArrayList<>();
                    for (Label label : currentLabels) {
                        if (!label.getId().equals(labelId)) {
                            updatedLabels.add(label);
                        }
                    }
                    labelsLiveData.postValue(updatedLabels);
                }
                isLoadingLiveData.postValue(false);
                Log.d(TAG, "Label deleted successfully: " + labelId);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue("Failed to delete label: " + error);
                isLoadingLiveData.postValue(false);
                Log.e(TAG, "Error deleting label: " + error);
            }
        });
    }

    /**
     * Update a label via API call
     */
    public void updateLabel(String labelId, String newLabelName) {
        isLoadingLiveData.postValue(true);

        // Create updated label object
        Label updatedLabel = new Label();
        updatedLabel.setId(labelId);
        updatedLabel.setName(newLabelName);

        apiService.updateLabel(labelId, updatedLabel, new LabelApiService.ApiCallback<Label>() {
            @Override
            public void onSuccess(Label updatedLabel) {
                // Update in memory after successful API call
                List<Label> currentLabels = labelsLiveData.getValue();
                if (currentLabels != null) {
                    for (Label label : currentLabels) {
                        if (label.getId().equals(labelId)) {
                            label.setName(updatedLabel.getName());
                            break;
                        }
                    }
                    // Trigger LiveData update
                    labelsLiveData.postValue(new ArrayList<>(currentLabels));
                }
                isLoadingLiveData.postValue(false);
                Log.d(TAG, "Label updated successfully: " + updatedLabel.getName());
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue("Failed to update label: " + error);
                isLoadingLiveData.postValue(false);
                Log.e(TAG, "Error updating label: " + error);
            }
        });
    }

    private void fetchFromApi() {
        isLoadingLiveData.postValue(true);

        apiService.fetchLabels(new LabelApiService.ApiCallback<List<Label>>() {
            @Override
            public void onSuccess(List<Label> labels) {
                // Store in memory instead of database
                labelsLiveData.postValue(labels);
                isLoadingLiveData.postValue(false);
                Log.d(TAG, "Labels fetched and stored in memory: " + labels.size() + " labels");
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void clearLabels() {
        labelsLiveData.postValue(new ArrayList<>());
    }
}