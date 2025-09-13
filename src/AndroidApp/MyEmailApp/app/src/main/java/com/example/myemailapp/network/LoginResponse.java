package com.example.myemailapp.network;

import com.example.myemailapp.model.User;

public class LoginResponse {
    private String message;
    private String token;
    private User userJson;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public User getUserJson() {
        return userJson;
    }

}
