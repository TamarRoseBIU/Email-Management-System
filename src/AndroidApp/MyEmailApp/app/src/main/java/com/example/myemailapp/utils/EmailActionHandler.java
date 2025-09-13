package com.example.myemailapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.myemailapp.BuildConfig;
import com.example.myemailapp.model.Email;
public class EmailActionHandler {
    private final Context context;
    private final String baseUrl;
    private final String token;

    public EmailActionHandler(Context context, String token) {
        this.context = context;
        this.token = token;
        this.baseUrl = BuildConfig.BASE_URL; // Adjust port as needed
    }

    // Interface for callbacks
    public interface ActionCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface EmailListCallback {
        void onEmailsUpdated(List<Email> emails);
    }

    // Determine mail type first
    private void determineMailType(String emailId, MailTypeCallback callback) {
        String TAG = "EmailActionHandler"; // Tag for logging
        Log.d(TAG, "Starting determineMailType for emailId: " + emailId);

        if (emailId == null || emailId.isEmpty()) {
            Log.e(TAG, "Error: emailId is null or empty");
            callback.onError("Invalid emailId: null or empty");
            return;
        }

        String url = baseUrl + "/objects/" + emailId;
        Log.d(TAG, "Constructed URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Received response for emailId: " + emailId + ", Response: " + response.toString());
                    try {
                        String type = response.getString("message");
                        Log.d(TAG, "Mail type found: " + type);
                        callback.onTypeFound(type);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error for emailId: " + emailId, e);
                        callback.onError("Failed to determine mail type: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMessage = "Object not found";
                    if (error.networkResponse != null) {
                        errorMessage += ", HTTP Status: " + error.networkResponse.statusCode;
                        Log.e(TAG, "Network error for emailId: " + emailId + ", Status Code: " + error.networkResponse.statusCode + ", Response: " + new String(error.networkResponse.data));
                    } else {
                        Log.e(TAG, "Network error for emailId: " + emailId + ", No network response available", error);
                    }
                    callback.onError(errorMessage);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                Log.d(TAG, "Headers set: Authorization=Bearer " + (token != null ? "[redacted]" : "null"));
                return headers;
            }
        };

        Log.d(TAG, "Adding request to Volley queue for emailId: " + emailId);
        Volley.newRequestQueue(context).add(request);
    }

    // Generic delete action
    public void deleteEmail(String emailId, ActionCallback callback) {
        determineMailType(emailId, new MailTypeCallback() {
            @Override
            public void onTypeFound(String type) {
                performDelete(emailId, type, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    private void performDelete(String emailId, String type, ActionCallback callback) {
        String url;
        int method;

        switch (type) {
            case "spam":
                url = baseUrl + "/trash/from-spam/" + emailId;
                method = Request.Method.POST;
                break;
            case "drafts":
                url = baseUrl + "/trash/from-draft/" + emailId;
                method = Request.Method.POST;
                break;
            case "mails":
                url = baseUrl + "/trash/from-inbox/" + emailId;
                method = Request.Method.POST;
                break;
            case "trash":
                url = baseUrl + "/trash/" + emailId;
                method = Request.Method.DELETE;
                break;
            default:
                callback.onError("Unknown mail type: " + type);
                return;
        }

        StringRequest request = new StringRequest(method, url,
                response -> callback.onSuccess(),
                error -> callback.onError("Delete failed: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    // Generic mark as read action
    public void markAsRead(String emailId, ActionCallback callback) {
        determineMailType(emailId, new MailTypeCallback() {
            @Override
            public void onTypeFound(String type) {
                performMarkAsRead(emailId, type, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    private void performMarkAsRead(String emailId, String type, ActionCallback callback) {
        String url = baseUrl + "/" + type + "/read/" + emailId;

        StringRequest request = new StringRequest(Request.Method.PATCH, url,
                response -> callback.onSuccess(),
                error -> callback.onError("Failed to mark as read: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    // Generic mark as unread action
    public void markAsUnread(String emailId, ActionCallback callback) {
        determineMailType(emailId, new MailTypeCallback() {
            @Override
            public void onTypeFound(String type) {
                performMarkAsUnread(emailId, type, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    private void performMarkAsUnread(String emailId, String type, ActionCallback callback) {
        String url = baseUrl + "/" + type + "/unread/" + emailId;

        StringRequest request = new StringRequest(Request.Method.PATCH, url,
                response -> callback.onSuccess(),
                error -> callback.onError("Failed to mark as unread: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    // Generic mark as spam action
    public void markAsSpam(String emailId, ActionCallback callback) {
        determineMailType(emailId, new MailTypeCallback() {
            @Override
            public void onTypeFound(String type) {
                if ("spam".equals(type)) {
                    callback.onError("Cannot mark this item as spam.");
                    return;
                }
                performMarkAsSpam(emailId, type, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    private void performMarkAsSpam(String emailId, String type, ActionCallback callback) {
        String url = baseUrl + "/spam/from-" + type + "/" + emailId;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> callback.onSuccess(),
                error -> callback.onError("Failed to mark as spam: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    // Generic toggle star action
    public void toggleStar(String emailId, boolean isCurrentlyStarred, ActionCallback callback) {
        determineMailType(emailId, new MailTypeCallback() {
            @Override
            public void onTypeFound(String type) {
                performToggleStar(emailId, type, isCurrentlyStarred, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    private void performToggleStar(String emailId, String type, boolean isCurrentlyStarred, ActionCallback callback) {
        String action = isCurrentlyStarred ? "unstar" : "star";
        String url = baseUrl + "/" + type + "/" + action + "/" + emailId;

        StringRequest request = new StringRequest(Request.Method.PATCH, url,
                response -> callback.onSuccess(),
                error -> callback.onError("Failed to " + action + " mail: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    // Generic save labels action
    public void saveLabels(String emailId, List<String> labels, ActionCallback callback) {
        determineMailType(emailId, new MailTypeCallback() {
            @Override
            public void onTypeFound(String type) {
                performSaveLabels(emailId, type, labels, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

//    private void performSaveLabels(String emailId, String type, List<String> labels, ActionCallback callback) {
//        String url = baseUrl + "/" + type + "/label/" + emailId;
//
//        JSONObject jsonBody = new JSONObject();
//        try {
//            JSONArray labelsArray = new JSONArray(labels);
//            jsonBody.put("labels", labelsArray);
//        } catch (JSONException e) {
//            callback.onError("Failed to create labels JSON");
//            return;
//        }
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, jsonBody,
//                response -> callback.onSuccess(),
//                error -> callback.onError("Failed to update labels: " + error.getMessage())
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + token);
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//
//        Volley.newRequestQueue(context).add(request);
//    }
private void performSaveLabels(String emailId, String type, List<String> labels, ActionCallback callback) {
    String url = baseUrl + "/" + type + "/label/" + emailId;

    JSONObject jsonBody = new JSONObject();
    try {
        JSONArray labelsArray = new JSONArray(labels);
        jsonBody.put("labels", labelsArray);
    } catch (JSONException e) {
        callback.onError("Failed to create labels JSON");
        return;
    }

    // Use StringRequest instead of JsonObjectRequest since we don't need to parse the response
    StringRequest request = new StringRequest(Request.Method.PATCH, url,
            response -> {
                // Success - response might be empty, which is fine
                callback.onSuccess();
            },
            error -> {
                Log.e("EmailActionHandler", "Failed to update labels", error);
                callback.onError("Failed to update labels: " + error.getMessage());
            }
    ) {
        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + token);
            headers.put("Content-Type", "application/json");
            return headers;
        }

        @Override
        public byte[] getBody() {
            try {
                return jsonBody.toString().getBytes("utf-8");
            } catch (Exception e) {
                Log.e("EmailActionHandler", "Error creating request body", e);
                return null;
            }
        }

        @Override
        public String getBodyContentType() {
            return "application/json; charset=utf-8";
        }
    };

    Volley.newRequestQueue(context).add(request);
}
    // Helper interface for mail type determination
    private interface MailTypeCallback {
        void onTypeFound(String type);
        void onError(String error);
    }

    // Utility method to show toast messages
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}