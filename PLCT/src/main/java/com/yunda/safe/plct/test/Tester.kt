package com.yunda.safe.plct.test

import com.elvishew.xlog.XLog
import com.yunda.safe.plct.api.ApiClient

object Tester {
    init {
        XLog.w("Testing...")
    }

    fun testApi() {
        val baseUrl = "http://10.60.0.66:8089"

        // 第一个请求
        ApiClient.getAsync("$baseUrl/api/users") { response, error ->
            if (error != null) {
                XLog.e("GET failed", error)
                return@getAsync
            }
            val result = response?.body?.string()
            XLog.i("GET ok: $result")
        }
    }
}