package com.example.zhangliang.videonews.jsbridge;


public interface LvUJsBridge {

    public void send(String data);

    public void send(String data, CallBackFunction responseCallback);


}
