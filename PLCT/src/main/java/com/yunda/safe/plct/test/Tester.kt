package com.yunda.safe.plct.test

import com.elvishew.xlog.XLog
import com.yunda.safe.plct.api.API
import com.yunda.safe.plct.api.ApiClient
import com.yunda.safe.plct.utility.DateTime

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

    fun testApi2() {
        val baseUrl = "http://10.60.0.66:9291"
        val api = "$baseUrl${API.SYSTEM_TIME}"
        ApiClient.getAsync(api) { response, error ->
            if (error != null) {
                XLog.e("GET failed", error)
                return@getAsync
            }
            var result = response?.body?.string()?.replace("\"", "")?.trim()
            // TODO: set android system time
            if (result != null && result.isNotEmpty()) {
                try {
                    result = "2025-06-06 06:06:06"
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val date = sdf.parse(result)
                    var exitCode = DateTime.setSysDateTime(date)
                    XLog.i("Set system time cmd: date $date, exitCode: $exitCode")
                } catch (e: Exception) {
                    XLog.e("Set system time failed: ${e.message}", e)
                }
            }
            XLog.i("GET ok: $result")
        }
    }
}