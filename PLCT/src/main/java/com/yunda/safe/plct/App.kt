package com.yunda.safe.plct

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.elvishew.xlog.XLog
import com.jakewharton.threetenabp.AndroidThreeTen
import com.yunda.safe.plct.common.APK_VERSION
import com.yunda.safe.plct.database.AppRepository
import com.yunda.safe.plct.handle.PollWorker
import com.yunda.safe.plct.utility.LogUtil
import com.yunda.safe.plct.utility.Preferences


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        LogUtil.init(this@App)

        val pm = this.packageManager
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pm.getPackageInfo(this.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(this.packageName, PackageManager.GET_SIGNATURES)
        }

        // 获取版本信息
        val versionName = info.versionName
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            info.versionCode.toLong()
        }

        XLog.i("APK versionName: $versionName, versionCode: $versionCode")

        // 获取签名信息
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.signingInfo?.apkContentsSigners ?: emptyArray()
        } else {
            @Suppress("DEPRECATION")
            info.signatures
        }
        signatures?.forEach {
            XLog.i("APK Signer: ${it.toCharsString()}")
        }

        // Tester.testApi2()

        Thread.setDefaultUncaughtExceptionHandler(GlobalCrashHandler(this@App))

        AndroidThreeTen.init(this@App)
        Preferences.init(this@App)
        AppRepository.initialize(this@App)

        PollWorker.start(this@App)

//        PollService.start(this@App)
        Preferences.saveString(APK_VERSION, versionName)
    }


    override fun onTerminate() {
        super.onTerminate()

        PollWorker.cancel(this@App)

//        PollService.stop(this@App)

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



