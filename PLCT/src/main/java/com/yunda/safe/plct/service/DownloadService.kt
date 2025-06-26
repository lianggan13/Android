package com.yunda.safe.plct.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.yunda.safe.plct.MainActivity
import com.yunda.safe.plct.handle.DownloadListener
import com.yunda.safe.plct.handle.DownloadTask
import java.io.File

class DownloadService : Service() {

    private var downloadTask: DownloadTask? = null
    private var downloadUrl: String? = null

    private val listener = object : DownloadListener {
        override fun onProgress(progress: Int) {
            getNotificationManager().notify(1, getNotification("Downloading...", progress))
        }

        override fun onSuccess() {
            downloadTask = null
            stopForeground(true)
            getNotificationManager().notify(1, getNotification("Download Success", -1))
            Toast.makeText(this@DownloadService, "Download Success", Toast.LENGTH_SHORT).show()

            // 假设 downloadUrl 不为空
            val fileName = downloadUrl?.substring(downloadUrl!!.lastIndexOf("/")) ?: return
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
            val apkFile = File(directory + fileName)
            installApk(apkFile)
        }

        private fun installApk(apkFile: File) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val apkUri = androidx.core.content.FileProvider.getUriForFile(
                this@DownloadService,
                "${packageName}.fileprovider",
                apkFile
            )
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }

        override fun onFailed() {
            downloadTask = null
            stopForeground(true)
            getNotificationManager().notify(1, getNotification("Download Failed", -1))
            Toast.makeText(this@DownloadService, "Download Failed", Toast.LENGTH_SHORT).show()
        }

        override fun onPaused() {
            downloadTask = null
            Toast.makeText(this@DownloadService, "Paused", Toast.LENGTH_SHORT).show()
        }

        override fun onCanceled() {
            downloadTask = null
            stopForeground(true)
            Toast.makeText(this@DownloadService, "Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private val mBinder = DownloadBinder()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    inner class DownloadBinder : Binder() {

        fun startDownload(url: String?) {
            if (downloadTask == null) {
                downloadUrl = url
                downloadTask = DownloadTask(listener)
                downloadTask?.execute(downloadUrl)
                startForeground(1, getNotification("Downloading...", 0))
                Toast.makeText(this@DownloadService, "Downloading...", Toast.LENGTH_SHORT).show()
            }
        }

        fun pauseDownload() {
            downloadTask?.pauseDownload()
        }

        fun cancelDownload() {
            if (downloadTask != null) {
                downloadTask?.cancelDownload()
            } else {
                if (downloadUrl != null) {
                    val fileName = downloadUrl!!.substring(downloadUrl!!.lastIndexOf("/"))
                    val directory =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                    val file = File(directory + fileName)
                    if (file.exists()) {
                        file.delete()
                    }
                    getNotificationManager().cancel(1)
                    stopForeground(true)
                    Toast.makeText(this@DownloadService, "Canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getNotificationManager(): NotificationManager {
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun getNotification(title: String, progress: Int): Notification {
        val channelId = "download_channel"
        val channelName = "下载通知"

        // Android 8.0+ 创建通知通道（幂等，已存在不会重复创建）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = getNotificationManager()
            // 检查通道是否已存在（可选，直接创建也没问题）
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = android.app.NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager.createNotificationChannel(channel)
            }
        }

        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(this.applicationInfo.icon)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    android.R.drawable.ic_menu_manage
                )
            )
            .setContentIntent(pi)
            .setContentTitle(title)

        if (progress >= 0) {
            builder.setContentText("$progress%")
            builder.setProgress(100, progress, false)
        }
        return builder.build()
    }
}