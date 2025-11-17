package com.yunda.safe.plct.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.elvishew.xlog.XLog
import com.yunda.safe.plct.common.Constants

class AlarmService() {
    companion object {
        /**
         * 根据时间戳设置一次性精确闹钟
         */
        fun setAlarm(context: Context, timeInMillis: Long) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(Constants.ACTION_REFRESH_WEBVIEW)
            val flag =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                else
                    PendingIntent.FLAG_UPDATE_CURRENT
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    flag
                )

            // 设置重复闹钟（每天），使用非精确重复以省电
            // alarmManager.setInexactRepeating(
            //     AlarmManager.RTC_WAKEUP,
            //     timeInMillis,
            //     AlarmManager.INTERVAL_DAY,
            //     pendingIntent
            // )
            // XLog.i("AlarmService: set daily alarm at ${time.hour}:${time.minute}:${time.second}")

            try {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            timeInMillis,
                            pendingIntent
                        )
                    }

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            timeInMillis,
                            pendingIntent
                        )
                    }

                    else -> {
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            timeInMillis,
                            pendingIntent
                        )
                    }
                }
                XLog.i("AlarmService: set one-shot alarm at ${java.util.Date(timeInMillis)}")
            } catch (se: SecurityException) {
                XLog.w("AlarmService: exact alarm denied (${se.message}), fallback to set()")
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } catch (e: Exception) {
                XLog.e("AlarmService: setAlarm failed: ${e.message}", e)
            }
        }

        fun cancelAlarm(context: Context) {
            val intent = Intent(Constants.ACTION_REFRESH_WEBVIEW)
            val flag =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                else
                    PendingIntent.FLAG_UPDATE_CURRENT
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    flag
                )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            XLog.i("AlarmService: cancelled alarm")
        }
    }
}