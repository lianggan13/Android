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
import com.yunda.safe.plct.common.Constants
import com.yunda.safe.plct.data.ApkVersion
import com.yunda.safe.plct.utility.BrowserLauncher
import com.yunda.safe.plct.utility.DateTime
import okhttp3.Response
import java.util.Locale
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
        val jobStart = System.currentTimeMillis()
        XLog.i("[PollWorker] Triggered at ${java.util.Date(jobStart)}")

        try {

            // 刷新网页页面
            // BrowserLauncher.waitForWebsiteAndLaunch(
            //     context = mContext,
            //     Preferences.getString(
            //         Constants.BROWSER_HOMEPAGE,
            //         Constants.DEFAULT_BROWSER_HOMEPAGE
            //     )!!
            // )

            // 同步服务时间
            try {
                val timeUrl = "${Constants.Host}${API.SYSTEM_TIME}"
                val t0 = System.currentTimeMillis()
                XLog.i("[PollWorker] Sync time: GET $timeUrl")
                ApiClient.getAsync(timeUrl) { response, error ->
                    val t1 = System.currentTimeMillis()
                    if (error != null) {
                        XLog.e("[PollWorker] GET $timeUrl failed after ${t1 - t0}ms", error)
                        return@getAsync
                    }
                    try {
                        val body = response?.body?.string()?.replace("\"", "")?.trim().orEmpty()
                        XLog.i("[PollWorker] GET $timeUrl completed in ${t1 - t0}ms, bodyLen=${body.length}")
                        if (body.isNotEmpty()) {
                            // val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val sdf = java.text.SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault()
                            ).apply {
                                isLenient = false
                            }
                            val date = sdf.parse(body)
                            val exitCode = DateTime.setSysDateTime(date!!)
                            XLog.i("[PollWorker] Set system time: date=$date exitCode=$exitCode")
                        }
                    } catch (e: Exception) {
                        XLog.e("[PollWorker] Parsing/setting system time failed", e)
                    } finally {
                        try {
                            response?.close()
                        } catch (_: Exception) {
                        }
                    }
                }
            } catch (e: Exception) {
                XLog.e("[PollWorker] Async time sync scheduling failed", e)
            }

            // 检查版本更新
            val params = hashMapOf<String, Object>("type" to Constants.Version as Object)
            val jsonBody = ApiClient.buildFormBody(params)
            val url = "${Constants.Host}${API.APP_VERSION}"
            XLog.i("[PollWorker] POST $url with params type=${Constants.Version}")

            val tReq = System.currentTimeMillis()
            val response: Response? = try {
                ApiClient.postSync(url, jsonBody, 10)
            } catch (e: Exception) {
                XLog.e("[PollWorker] Request version check failed", e)
                return Result.success()
            }

            val tResp = System.currentTimeMillis()
            if (response == null) {
                XLog.e("[PollWorker] POST $url returned null after ${tResp - tReq}ms")
                return Result.retry()
            } else {
                response.use { resp ->
                    val code = resp.code
                    val bodyStr = try {
                        resp.body?.string().orEmpty()
                    } catch (e: Exception) {
                        XLog.e("[PollWorker] Read response body failed", e); ""
                    }
                    XLog.i("[PollWorker] POST $url completed in ${tResp - tReq}ms, http=$code, bodyLen=${bodyStr.length}")

                    if (!resp.isSuccessful) {
                        XLog.e("[PollWorker] POST $url http error ${resp.code}: ${resp.message}")
                        return Result.retry()
                    }

                    try {
                        XLog.i("[PollWorker] API Version response preview: ${bodyStr.take(500)}")
                        val apkVersion = Gson().fromJson(bodyStr, ApkVersion::class.java)
                        if (apkVersion == null) {
                            XLog.e("[PollWorker] Failed to parse ApkVersion from response")
                            return Result.retry()
                        }

                        XLog.i("[PollWorker] Local version=${Constants.Version}, Server version=${apkVersion.versionNo}")
                        if (Constants.Version != apkVersion.versionNo) {
                            if (Constants.ServerVersion != apkVersion.versionNo) {
                                Constants.ServerVersion = apkVersion.versionNo

                                XLog.i("[PollWorker] New version detected, broadcasting update notification")

                                broadcast(mContext, apkVersion)
                            }
                        } else {
                            XLog.i("[PollWorker] Version is up to date")
                        }
                    } catch (e: Exception) {
                        XLog.e("[PollWorker] Error parsing JSON response", e)
                        return Result.retry()
                    }
                }
            }

            val jobEnd = System.currentTimeMillis()
            XLog.i("[PollWorker] doWork finished successfully in ${jobEnd - jobStart}ms")
            return Result.success()
        } catch (e: Exception) {
            XLog.e("[PollWorker] Unexpected error in doWork", e)
            return Result.retry()
        }
    }

    private fun broadcast(
        context: Context,
        apkVersion: ApkVersion
    ) {

        BrowserLauncher.closeEdgeBrowser()
        // 强制将 App 切换到前台主活动页
        bringAppToForeground(context, apkVersion)

        // 使用 Handler 延迟发送广播，确保 Activity 已经启动
        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(Constants.ACTION_SHOW_SHOW_NOTIFICATION).apply {
                putExtra(Constants.APK_VERSION, apkVersion)
            }

            //  mContext.sendBroadcast(intent)

            // 发送 带权限限制 + 有序 的广播：
            // 1.阻⽌未授权的其它 App 监听和触发自己的 BroadcastReceiver
            // 2.解决 NotificationReceiver ⽆法在新版本系统上⼯作的问题
            // 3.根据优先级 保证 broadcast⼀次⼀个地投递给各个 receiver
            context.sendOrderedBroadcast(intent, Constants.PERMISSION_PRIVATE)

            XLog.i("[PollWorker] Update broadcast sent after bringing app to foreground")
        }, 800) // 延迟800ms确保Activity启动完成
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