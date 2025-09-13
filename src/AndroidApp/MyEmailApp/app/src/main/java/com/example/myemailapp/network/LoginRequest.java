package com.example.myemailapp.network;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Required for Retrofit's JSON parsing
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}