package com.yunda.safe.plct.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.elvishew.xlog.XLog

class PollService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            XLog.i("[PollService] poll run")

//            NotifyService.showNotification2(this@PollService);
            handler.postDelayed(this, 5000) // 高频轮询耗电大
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PollService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, PollService::class.java)
            context.stopService(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(runnable)
        // 可选：启动前台通知，防止服务被杀死
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}