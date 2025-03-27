package com.example.wechart.jsbridge;


public interface WebViewJavascriptBridge {

    void sendToWeb(String data);

    void sendToWeb(String data, OnBridgeCallback responseCallback);

    void sendToWeb(String function, Object... values);

}
