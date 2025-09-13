package com.example.myemailapp.network;

public class UsernameCheckResponse {
    private boolean exists;
    private String message;

    // Constructors
    public UsernameCheckResponse() {}

    public UsernameCheckResponse(boolean exists, String message) {
        this.exists = exists;
        this.message = message;
    }

    // Getters and Setters
    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}