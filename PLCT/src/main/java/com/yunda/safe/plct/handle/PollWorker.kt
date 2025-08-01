package com.yunda.safe.plct.handle

import android.content.Context
import android.content.Intent
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
import com.yunda.safe.plct.utility.Preferences
import java.util.concurrent.TimeUnit

class PollWorker(private val mContext: Context, workerParameters: WorkerParameters) :
    Worker(mContext, workerParameters) {

    companion object {

        fun start(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // CONNECTED:任何已连接的网络
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

            val baseUrl = Preferences.getString(
                SERVER_HOST,
                DEFAULT_SERVER_HOST
            )
            var version = Preferences.getString(
                APK_VERSION,
                DEFAULT_SOFTWARE_VERSION
            )
            val params = hashMapOf<String, Object>(
                "version" to version as Object
            )
            val jsonBody = ApiClient.buildJsonBody(params)
            ApiClient.postAsync(
                "$baseUrl${API.APP_VERSION}",
                jsonBody
            ) { response, error2 ->
                if (error2 != null) {
                    XLog.e("POST failed", error2)
                    return@postAsync
                }
                val result2 = response?.body?.string()
                XLog.i("POST ok: $result2")
//                val json = org.json.JSONObject(result2)
//                val pkg = json.optString("package")
//                val version = json.optString("version")
//                val success = json.optBoolean("success")
                val apkVersion = Gson().fromJson(result2, ApkVersion::class.java)
                if (apkVersion.success) {
                    if (version != apkVersion.version) {
//                        NotifyService.Notify(mContext);
                        broadcast(mContext, apkVersion)
                    }
                }
            }
            return Result.success()

        } catch (e: Exception) {
            XLog.e(e.message, e);
            return Result.retry()
        }
    }

    private fun broadcast(
        context: Context,
        apkVersion: ApkVersion
    ) {
        val intent = Intent(ACTION_SHOW_SHOW_NOTIFICATION).apply {
            putExtra(APK_VERSION, apkVersion)
        }
//        mContext.sendBroadcast(intent)

        //发送 带权限限制 + 有序 的广播：
        //1.阻⽌未授权的其它 App 监听和触发自己的 BroadcastReceiver
        //2.解决 NotificationReceiver ⽆法在新版本系统上⼯作的问题
        //3.根据优先级 保证 broadcast⼀次⼀个地投递给各个 receiver
        context.sendOrderedBroadcast(intent, PERMISSION_PRIVATE)
    }
}