package com.yunda.safe.plct

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yunda.safe.plct.database.AppRepository
import com.yunda.safe.plct.utility.AppPreferences
import com.yunda.safe.plct.work.PollWorker
import java.util.concurrent.TimeUnit

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(this))

        AppPreferences.init(this@App)
        AppRepository.initialize(this@App)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // CONNECTED:任何已连接的网络
            .build()

        val workRequest = PeriodicWorkRequestBuilder<PollWorker>(15, TimeUnit.MINUTES) // 每15分钟执行一次
            .setConstraints(constraints)
            .build()

        // 将工作排队
        WorkManager.getInstance(this@App).enqueueUniquePeriodicWork(
            com.yunda.safe.plct.work.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }


    override fun onTerminate() {
        super.onTerminate()
        AppRepository.get().close()

        WorkManager.getInstance(this@App).cancelAllWork()
    }

    inner class GlobalExceptionHandler(private val context: Context) :
        Thread.UncaughtExceptionHandler {

        private val defaultHandler: Thread.UncaughtExceptionHandler? =
            Thread.getDefaultUncaughtExceptionHandler()

        override fun uncaughtException(thread: Thread, throwable: Throwable) {
            val stackTrace = throwable.stackTraceToString()

            // 在主线程显示 AlertDialog
            Handler(Looper.getMainLooper()).post {
                AlertDialog.Builder(context)
                    .setTitle("异常")
                    .setMessage(stackTrace)
                    .setPositiveButton("确定") { _, _ ->
                        // 结束应用或做其他处理
                        defaultHandler?.uncaughtException(thread, throwable)
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}