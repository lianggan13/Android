package com.example.zhangliang.videoprojects.api;

public interface HttpCallback {

    void onSuccess(String res);

    void onFailure(Exception e);
}
