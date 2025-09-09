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

    fun testApi3() {
        val code = 29
        val baseUrl = "http://10.60.0.66:9291"
        val api = "$baseUrl${API.SYSTEM_RST}/${code}"

        // val url = "${Constants.Host}${API.SYSTEM_CODE}/$code"
        // val response = ApiClient.postSync(api, null, 10)  // POST 请求，body 为空，code  作为路径参数
        // if (response == null || !response.isSuccessful) {
        //     XLog.e("Failed to get restart time setting")
        //     return
        // }

        ApiClient.postAsync(api, null, 10) { resp, error ->
            if (error != null) {
                XLog.e("GET failed", error)

            }
            if (resp == null || !resp.isSuccessful) {
                XLog.e("GetByCode failed: ${resp?.code} ${resp?.message}")
            }
            // var body = response?.body?.string()?.replace("\"", "")?.trim()
            val body = resp!!.body?.string().orEmpty()
            var timeStr = org.json.JSONObject(body).optString("value").replace("\"", "").trim()
            if (timeStr.isEmpty()) {
                XLog.e("value is empty in response: $body")
            }

            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
            val sdf =
                java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())

            timeStr = "19:13:20"
            var next = sdf.parse("$today $timeStr")
            if (next == null) {
                XLog.e("parse failed: $today $timeStr")
            }

            if (next.time <= System.currentTimeMillis()) {
                val cal = java.util.Calendar.getInstance()
                cal.time = next
                cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
                next = cal.time
            }

            val delay = (next.time - System.currentTimeMillis()).coerceAtLeast(0)
            XLog.i("下次刷新时间：$next")
            XLog.i("距下次刷新剩余${delay / 1000}秒")

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                // TODO: 这里执行你的动作（如刷新页面/重启 Activity/发广播等）
                XLog.i("do refresh")
            }, delay)

            // XLog.i("GET ok: $result")
        }
    }
}