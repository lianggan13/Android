package com.yunda.safe.plct.api

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    private val defaultClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
    
    private fun getClient(timeoutSeconds: Int = 10): OkHttpClient {
        if (timeoutSeconds == 10) {
            return defaultClient
        }
        return OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .build()
    }

    fun get(url: String, timeoutSeconds: Int = 10): Response? {
        val request = Request.Builder()
            .url(url)
            .build()
        return getClient(timeoutSeconds).newCall(request).execute()
    }

    fun post(url: String, body: RequestBody, timeoutSeconds: Int = 10): Response? {
        val request = Request.Builder()
            .url(url)
            .addHeader("contentType", "application/json;charset=UTF-8")
            .post(body)
            .build()
        return getClient(timeoutSeconds).newCall(request).execute()
    }

    fun postSync(url: String, body: RequestBody, timeoutSeconds: Int = 10): Response? {
        val request = Request.Builder()
            .url(url)
            .addHeader("contentType", "application/json;charset=UTF-8")
            .post(body)
            .build()
        return getClient(timeoutSeconds).newCall(request).execute()
    }

    fun getAsync(url: String, timeoutSeconds: Int = 10, callback: (Response?, Exception?) -> Unit) {
        val request = Request.Builder()
            .url(url)
            // .addHeader("token", token)
            .build()
        getClient(timeoutSeconds).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    callback(it, null)
                }
            }
        })
    }

    fun postAsync(
        url: String,
        body: RequestBody,
        timeoutSeconds: Int = 10,
        callback: (Response?, Exception?) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .addHeader("contentType", "application/json;charset=UTF-8")
            .post(body)
            .build()
        getClient(timeoutSeconds).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    callback(it, null)
                }
            }
        })
    }

    fun buildJsonBody(params: HashMap<String, Object>): RequestBody {
        val jsonObject = org.json.JSONObject(params)
        val jsonStr = jsonObject.toString()
        return jsonStr.toRequestBody("application/json; charset=utf-8".toMediaType())
    }

    fun buildFormBody(params: HashMap<String, Object>): RequestBody {
        val formBuilder = okhttp3.FormBody.Builder()
        for ((key, value) in params) {
            formBuilder.add(key, value.toString())
        }
        return formBuilder.build()
    }
}