package com.example.zhangliang.videonews.api;

public interface HttpCallback {

    void onSuccess(String res);

    void onFailure(Exception e);
}
