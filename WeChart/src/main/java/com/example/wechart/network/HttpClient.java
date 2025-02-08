package com.example.wechart.network;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {
    public void requestOkhttp(String urlString) {
        // 创建OkHttpClient实例
        OkHttpClient client = new OkHttpClient();

        // 创建一个Request对象
        Request request = new Request.Builder()
                .url(urlString) // 这里替换成你要访问的URL
                .build();

        // 发送请求并处理响应
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
//                runOnUiThread(() -> contentTv.setText(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
//                runOnUiThread(() -> {
                if (response.isSuccessful()) {
                    String responseBody = null;
                    try {
                        responseBody = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("http", responseBody);
                } else {
                    Log.e("http", "请求失败：" + response.code());
                }
//                });
            }
        });
    }


    public void okhttpPost(String urlString) {
        FormBody body = new FormBody.Builder()
                .add("username", "zhangsan")
                .add("password", "123456")
                .build();

        Request request = new Request.Builder()
                .url(urlString)
                .post(body)
                .build();

        // 创建OkHttpClient实例
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}
