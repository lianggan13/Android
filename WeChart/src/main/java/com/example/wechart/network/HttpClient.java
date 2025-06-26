package com.example.wechart.network;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    private static OkHttpClient client;
    private static String requestUrl;
    private static HashMap<String, Object> mParams;

    public static HttpClient api = new HttpClient();

    public HttpClient() {

    }

    public static HttpClient config(String url, HashMap<String, Object> params) {
        client = new OkHttpClient.Builder()
                .build();
        requestUrl = url;
        mParams = params;
        return api;
    }

    public void postRequest(Context context, Supplier<RequestBody> bodySupplier, final HttpCallback callback) {
        RequestBody body = bodySupplier.get();
        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("contentType", "application/json;charset=UTF-8")
                .post(body)
                .build();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response);
            }
        });
    }

    public void getRequest(Context context, final HttpCallback callback) {
        String url = getAppendUrl(requestUrl, mParams);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response);
            }
        });
    }

    private String getAppendUrl(String url, Map<String, Object> map) {
        if (map != null && !map.isEmpty()) {
            StringBuffer buffer = new StringBuffer();
            Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                if (buffer.toString().isEmpty()) {
                    buffer.append("?");
                } else {
                    buffer.append("&");
                }
                buffer.append(entry.getKey()).append("=").append(entry.getValue());
            }
            url += buffer.toString();
        }
        return url;
    }

    public static RequestBody buildJsonBody() {
        JSONObject jsonObject = new JSONObject(mParams);
        String jsonStr = jsonObject.toString();
        RequestBody requestBodyJson =
                RequestBody.create(MediaType.parse("application/json;charset=utf-8")
                        , jsonStr);
        return requestBodyJson;
    }

    public static RequestBody buildFormBody() {
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (HashMap.Entry<String, Object> entry : mParams.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue().toString());
        }
        FormBody formBody = formBuilder.build();
        return formBody;
    }

    public interface HttpCallback {

        void onSuccess(Response resp);

        void onFailure(Exception e);
    }
}

