package com.example.zhangliang.videonews.entity;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("data")
    private UserData data;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    // Getters and Setters
    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return this.data.getPhotoUrl();
    }
}

