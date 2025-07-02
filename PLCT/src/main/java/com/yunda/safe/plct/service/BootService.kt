package com.yunda.safe.plct.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.yunda.safe.plct.MainActivity
import java.util.Timer
import java.util.TimerTask


class BootService : Service() {
    private var timer: Timer? = null
    private val isBootCompleted: Boolean
        get() =// 检查设备状态，例如通过读取系统属性或查询系统服务
            true // 假设设备状态正常

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 开启一个定时器，每隔一段时间检查设备状态
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                // 检查设备状态
                if (isBootCompleted) {
                    // 启动我们的应用
                    val launchIntent = Intent(
                        applicationContext,
                        MainActivity::class.java
                    )
                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(launchIntent)
                }
            }
        }, 0, 1000) // 每隔1秒检查一次设备状态
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        this.isBootCompleted
        // 取消定时器
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


}