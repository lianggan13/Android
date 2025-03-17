package com.yunda.safe.plct.work

import android.content.Context
import android.content.Intent
import android.util.Log
import com.yunda.safe.plct.BuildConfig

const val TAG = "PollWorker"

class PollWorker(private val mContext: Context, workerParameters: androidx.work.WorkerParameters) :
    androidx.work.Worker(mContext, workerParameters) {

    override fun doWork(): Result {
        Log.i(TAG, "Poll worker [doWork] triggered")


        // 发送广播，告诉 Activity 刷新 WebView
        val intent = Intent(ACTION_REFRESH_WEBVIEW).apply {
//            putExtra(REQUEST_CODE, requestCode)
//            putExtra(NOTIFICATION, notification)
        }

        mContext.sendBroadcast(intent)

        return Result.retry()
        return Result.success()
    }

    companion object {
        const val ACTION_REFRESH_WEBVIEW = BuildConfig.APPLICATION_ID + ".SHOW_NOTIFICATION"
        const val PERMISSION_PRIVATE = BuildConfig.APPLICATION_ID + ".PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
    }
}