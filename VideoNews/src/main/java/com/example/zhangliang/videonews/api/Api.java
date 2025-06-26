package com.example.zhangliang.videonews.api;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.zhangliang.videonews.activity.LoginActivity;
import com.example.zhangliang.videonews.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {
    private static OkHttpClient client;
    private static String requestUrl;
    private static HashMap<String, Object> mParams;

    public static Api api = new Api();

    public Api() {

    }

    public static Api config(String url, HashMap<String, Object> params) {
        client = new OkHttpClient.Builder()
                .build();
        requestUrl = ApiConfig.BASE_URl + url;
        mParams = params;
        return api;
    }

    public void postRequest(Context context, Supplier<RequestBody> bodySupplier, final HttpCallback callback) {
        SharedPreferences sp = context.getSharedPreferences("sp_ttit", MODE_PRIVATE);
        String token = sp.getString("token", "");
        RequestBody body = bodySupplier.get();
        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("contentType", "application/json;charset=UTF-8")
                .addHeader("token", token)
                .post(body)
                .build();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure", e.getMessage());
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                callback.onSuccess(result);
            }
        });
    }

    public void getRequest(Context context, final HttpCallback callback) {
        SharedPreferences sp = context.getSharedPreferences("sp_ttit", MODE_PRIVATE);
        String token = sp.getString("token", "");
        String url = getAppendUrl(requestUrl, mParams);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure", e.getMessage());
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if (code.equals("401")) {
                        // Token 401 -- 未授权 --> 登录页面
                        Intent in = new Intent(context, LoginActivity.class);
                        context.startActivity(in);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(result);
            }
        });
    }

    private String getAppendUrl(String url, Map<String, Object> map) {
        if (map != null && !map.isEmpty()) {
            StringBuffer buffer = new StringBuffer();
            Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                if (StringUtils.isEmpty(buffer.toString())) {
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
}

