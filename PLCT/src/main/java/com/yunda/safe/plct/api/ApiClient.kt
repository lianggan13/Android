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

object ApiClient {
    private val client = OkHttpClient()

    fun get(url: String): Response? {
        val request = Request.Builder()
            .url(url)
            .build()
        return client.newCall(request).execute()
    }

    fun post(url: String, body: RequestBody): Response? {
        val request = Request.Builder()
            .url(url)
            .addHeader("contentType", "application/json;charset=UTF-8")
            .post(body)
            .build()
        return client.newCall(request).execute()
    }

    fun getAsync(url: String, callback: (Response?, Exception?) -> Unit) {
        val request = Request.Builder()
            .url(url)
            // .addHeader("token", token)
            .build()
        client.newCall(request).enqueue(object : Callback {
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
        callback: (Response?, Exception?) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .addHeader("contentType", "application/json;charset=UTF-8")
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
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