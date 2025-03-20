package com.yunda.safe.plct.work

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yunda.safe.plct.BuildConfig
import java.util.concurrent.TimeUnit

const val TAG = "PollWorker"

class PollWorker(private val mContext: Context, workerParameters: androidx.work.WorkerParameters) :
    androidx.work.Worker(mContext, workerParameters) {

    companion object {
        const val ACTION_REFRESH_WEBVIEW = BuildConfig.APPLICATION_ID + ".SHOW_NOTIFICATION"
        const val PERMISSION_PRIVATE = BuildConfig.APPLICATION_ID + ".PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"

        fun start(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // CONNECTED:任何已连接的网络
                .build()

            val workRequest =
                PeriodicWorkRequestBuilder<PollWorker>(15, TimeUnit.MINUTES) // 每15分钟执行一次
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelAllWork()
        }
    }

    override fun doWork(): Result {
        Log.i(TAG, "Poll worker [doWork] triggered")

        try {
            // 发送广播，告诉 Activity 刷新 WebView
            val intent = Intent(ACTION_REFRESH_WEBVIEW).apply {
//            putExtra(REQUEST_CODE, requestCode)
//            putExtra(NOTIFICATION, notification)
            }

            mContext.sendBroadcast(intent)
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e);
            return Result.retry()
        }
    }

}