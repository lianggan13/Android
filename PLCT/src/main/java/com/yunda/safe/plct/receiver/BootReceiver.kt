package com.yunda.safe.plct.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.elvishew.xlog.XLog
import com.yunda.safe.plct.MainActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        XLog.i("BootReceiver", "onReceived")
        // if (Objects.requireNonNull<String?>(intent.action) == ACTION) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            XLog.i("BootComplete", "System boot completed")

            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

            // 或者启动后台服务
            // val serviceIntent = Intent(context, MyBackgroundService::class.java)
            // context.startService(serviceIntent)
        }

//        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
//            // 启动你的服务或活动
//            val serviceIntent = Intent(context, MyAppLaunchService::class.java)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent)
//            } else {
//                context.startService(serviceIntent)
//            }
//        }
    }
}



