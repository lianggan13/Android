package com.yunda.safe.plct

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.yunda.safe.plct.database.AppRepository
import com.yunda.safe.plct.utility.AppPreferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(this))

        AppPreferences.init(this@App)
        AppRepository.initialize(this@App)
    }

    override fun onTerminate() {
        super.onTerminate()
        AppRepository.get().close()
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