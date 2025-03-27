package com.yunda.safe.plct

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.elvishew.xlog.XLog
import com.jakewharton.threetenabp.AndroidThreeTen
import com.yunda.safe.plct.database.AppRepository
import com.yunda.safe.plct.utility.Logger
import com.yunda.safe.plct.utility.Preferences


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.init(this@App)
        XLog.i("App onCreated.")

//        val intent = Intent(applicationContext, BootService::class.java)
//        startService(intent)

//        val filter = IntentFilter(Intent.ACTION_BOOT_COMPLETED)
//        registerReceiver(BootReceiver(), filter)

        Thread.setDefaultUncaughtExceptionHandler(GlobalCrashHandler(this@App))

        AndroidThreeTen.init(this@App)
        Preferences.init(this@App)
        AppRepository.initialize(this@App)
    }

    override fun onTerminate() {
        super.onTerminate()
        AppRepository.get().close()
    }

    fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

        System.exit(0)
    }
}

class GlobalCrashHandler(private val mContext: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // 1. 记录崩溃日志
        logCrash(throwable)
        // 2. 上传到服务器
        uploadCrash(throwable)
        // 3. 优雅退出（可选重启）
        exitAppGracefully()
        // 4. 调用默认处理器（触发崩溃弹窗）
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun logCrash(e: Throwable) {
        XLog.e(e.message, e)
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(mContext, e.message, Toast.LENGTH_LONG).show()
        }
        Thread.sleep(2000)
    }

    private fun uploadCrash(e: Throwable) {
        // 使用 Retrofit 或 OkHttp 上报到服务器
    }

    private fun exitAppGracefully() {
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)

        Handler(Looper.getMainLooper()).post {
            XLog.i("App starting...")

            val myApp: App = mContext as App
            myApp.restartApp()
        }
    }
}



