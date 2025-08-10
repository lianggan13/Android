package com.yunda.safe.plct.handle

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.yunda.safe.plct.api.API
import com.yunda.safe.plct.api.ApiClient
import com.yunda.safe.plct.common.ACTION_SHOW_SHOW_NOTIFICATION
import com.yunda.safe.plct.common.APK_VERSION
import com.yunda.safe.plct.common.DEFAULT_SERVER_HOST
import com.yunda.safe.plct.common.DEFAULT_SOFTWARE_VERSION
import com.yunda.safe.plct.common.PERMISSION_PRIVATE
import com.yunda.safe.plct.common.SERVER_HOST
import com.yunda.safe.plct.data.ApkVersion
import com.yunda.safe.plct.utility.BrowserLauncher
import com.yunda.safe.plct.utility.DateTime
import com.yunda.safe.plct.utility.Preferences
import java.util.concurrent.TimeUnit

class PollWorker(private val mContext: Context, workerParameters: WorkerParameters) :
    Worker(mContext, workerParameters) {

    companion object {

        fun start(context: Context) {
            val constraints = Constraints.Builder()
                // .setRequiredNetworkType(NetworkType.CONNECTED) // CONNECTED:任何已连接的网络
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val workRequest =
                PeriodicWorkRequestBuilder<PollWorker>(15, TimeUnit.MINUTES) // 每15分钟执行一次
                    .setConstraints(constraints)
                    .build()

            WorkManager.Companion.getInstance(context).enqueueUniquePeriodicWork(
                "WorkQueue",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.Companion.getInstance(context).cancelAllWork()
        }
    }

    override fun doWork(): Result {
        try {
            XLog.i("[PollWorker] triggered...")

            // val browserHomepage = Preferences.getString(BROWSER_HOMEPAGE, DEFAULT_BROWSER_HOMEPAGE)
            // XLog.i("[PollWorker] Browser homepage: $browserHomepage")

            // 调用工具类的方法，后台服务不显示Toast
            BrowserLauncher.launchBrowserWithHomepage(
                context = mContext,
                showToast = false  // 后台服务不显示Toast
            )

            val baseUrl = Preferences.getString(
                SERVER_HOST,
                DEFAULT_SERVER_HOST
            )
            var version = Preferences.getString(
                APK_VERSION,
                DEFAULT_SOFTWARE_VERSION
            )
            val params = hashMapOf<String, Object>(
                "type" to version as Object
            )

            // 同步 NTP 服务时间
            ApiClient.getAsync("$baseUrl${API.SYSTEM_TIME}") { response, error ->
                if (error != null) {
                    XLog.e("GET failed", error)
                    return@getAsync
                }
                var result = response?.body?.string()?.replace("\"", "")?.trim()
                if (result != null && result.isNotEmpty()) {
                    try {
                        result = "2025-06-06 06:06:06"
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = sdf.parse(result)
                        var exitCode = DateTime.setSysDateTime(date)
                        XLog.i("[PollWorker] Set system time cmd: date $date, exitCode: $exitCode")
                    } catch (e: Exception) {
                        XLog.e("[PollWorker] Set system time failed: ${e.message}", e)
                    }
                }
            }

            // 检查版本更新
            //  val jsonBody = ApiClient.buildJsonBody(params)
            val jsonBody = ApiClient.buildFormBody(params)
            val response = ApiClient.postSync("$baseUrl${API.APP_VERSION}", jsonBody, 10)
            if (response == null || !response!!.isSuccessful) {
                XLog.e("[PollWorker] POST request failed: HTTP ${response?.code}")
                return Result.retry()
            }

            try {
                val result = response.body?.string()
                
                XLog.i("[PollWorker] API Version response: $result")

                val apkVersion = Gson().fromJson(result, ApkVersion::class.java)
                if (apkVersion != null) {
                    XLog.i("[PollWorker] Current version: $version, Server version: ${apkVersion.versionNo}")
                    if (version != apkVersion.versionNo) {
                        XLog.i("[PollWorker] New version detected, broadcasting update notification")
                        broadcast(mContext, apkVersion)
                    } else {
                        XLog.i("[PollWorker] Version is up to date")
                    }
                } else {
                    XLog.e("[PollWorker] Failed to parse ApkVersion from response")
                    return Result.retry()
                }
            } catch (e: Exception) {
                XLog.e("[PollWorker] Error parsing JSON response: ${e.message}", e)
                return Result.retry()
            }

            return Result.success()

        } catch (e: Exception) {
            XLog.e("[PollWorker] Unexpected error in doWork: ${e.message}", e)
            return Result.retry()
        }
    }

    private fun broadcast(
        context: Context,
        apkVersion: ApkVersion
    ) {
        // 先强制将 App 切换到前台主活动页
        bringAppToForeground(context, apkVersion)

        // 使用 Handler 延迟发送广播，确保 Activity 已经启动
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(ACTION_SHOW_SHOW_NOTIFICATION).apply {
                putExtra(APK_VERSION, apkVersion)
            }

            //  mContext.sendBroadcast(intent)

            //发送 带权限限制 + 有序 的广播：
            //1.阻⽌未授权的其它 App 监听和触发自己的 BroadcastReceiver
            //2.解决 NotificationReceiver ⽆法在新版本系统上⼯作的问题
            //3.根据优先级 保证 broadcast⼀次⼀个地投递给各个 receiver
            context.sendOrderedBroadcast(intent, PERMISSION_PRIVATE)

            XLog.i("[PollWorker] Update broadcast sent after bringing app to foreground")
        }, 800) // 延迟800ms确保Activity启动完成

        XLog.i("[PollWorker] App brought to foreground, broadcast scheduled")
    }

    /**
     * 强制将应用切换到前台主活动页
     */
    private fun bringAppToForeground(context: Context, apkVersion: ApkVersion) {
        try {
            XLog.i("[PollWorker] Bringing app to foreground...")

            val packageManager = context.packageManager
            val launchIntent = packageManager.getLaunchIntentForPackage(context.packageName)

            if (launchIntent != null) {
                launchIntent.apply {
                    // 设置启动标志
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)

                    // 添加额外信息，表明这是从更新检查启动的
                    putExtra("from_update_check", true)
                    putExtra("update_version", apkVersion.versionNo)
                }

                context.startActivity(launchIntent)
                XLog.i("[PollWorker] Successfully launched main activity")
            } else {
                XLog.e("[PollWorker] Could not get launch intent for package: ${context.packageName}")
            }
        } catch (e: Exception) {
            XLog.e("[PollWorker] Failed to bring app to foreground: ${e.message}", e)
        }
    }
}