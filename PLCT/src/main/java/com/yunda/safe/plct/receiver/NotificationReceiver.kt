package com.yunda.safe.plct.receiver

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.elvishew.xlog.XLog
import com.yunda.safe.plct.common.StringConstants

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        XLog.d("onReceive: receive  result = $resultCode")
        if (resultCode != Activity.RESULT_OK) {
            // A foreground activity canceled the broadcast
            return
        }
        val requestCode = intent.getIntExtra(StringConstants.REQUEST_CODE, 0)
        val notification: Notification? =
            intent.getParcelableExtra(StringConstants.NOTIFICATION)
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(requestCode, notification ?: return)
    }
}