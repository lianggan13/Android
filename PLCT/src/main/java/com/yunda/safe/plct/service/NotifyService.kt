package com.yunda.safe.plct.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.yunda.safe.plct.MainActivity

object NotifyService {
    val channelName = "channel_name"
    var channelId = "channel_id"

    public fun notify(context: Context) {
        // 1. 确保通知通道已注册（Android 8.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                notificationManager.createNotificationChannel(channel)
            }
        }

        // 2. 构建 PendingIntent
        val newIntent = Intent(context, MainActivity::class.java)
        val flag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getActivity(context, 0, newIntent, flag)

        // 3. 构建通知
        val title = "New PLCT Version"
        val text = "Click and Update PLCT Version"
        val resources = context.resources
        val notification = NotificationCompat.Builder(context, channelId)
            .setTicker("ticker")
            .setSmallIcon(context.applicationInfo.icon)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    android.R.drawable.ic_menu_manage
                )
            )
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // 4. 权限检查并发送通知
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 没有通知权限，直接返回
            return
        }
        notificationManager.notify(0, notification)
    }
}