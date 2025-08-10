package com.yunda.safe.plct.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.elvishew.xlog.XLog
import com.yunda.safe.plct.MainActivity
import com.yunda.safe.plct.common.ACTION_SHOW_SHOW_NOTIFICATION
import com.yunda.safe.plct.common.APK_VERSION
import com.yunda.safe.plct.data.ApkVersion

class AppUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        return

        if (intent.action == ACTION_SHOW_SHOW_NOTIFICATION) {
            val apkVersion = intent.getSerializableExtra(APK_VERSION) as? ApkVersion

            XLog.i("AppUpdateReceiver: Received update notification, version: ${apkVersion?.versionNo}")

            // 只显示系统通知，不显示 AlertDialog
            showUpdateNotification(context, apkVersion)
        }
    }

    private fun showUpdateNotification(context: Context, apkVersion: ApkVersion?) {
        if (apkVersion == null) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 创建通知渠道（Android 8.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "app_update",
                "应用更新",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "应用版本更新通知"
                setShowBadge(true)
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 创建启动 MainActivity 的 Intent
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", "show_update_dialog")
            putExtra(APK_VERSION, apkVersion)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 构建通知
        val notification = NotificationCompat.Builder(context, "app_update")
            .setSmallIcon(context.applicationInfo.icon)
            .setContentTitle("应用更新")
            .setContentText("检测到新版本 ${apkVersion.versionNo}，点击查看详情")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SYSTEM)
            .build()

        notificationManager.notify(1001, notification)

        XLog.i("Update notification displayed for version: ${apkVersion.versionNo}")
    }
}