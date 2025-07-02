package com.yunda.safe.plct.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.yunda.safe.plct.common.ACTION_REFRESH_WEBVIEW
import java.util.concurrent.ConcurrentHashMap


class RefreshReceiver(private val callback: (Context?, Intent?) -> Unit) : BroadcastReceiver() {

    companion object {
        private val receiverMap = ConcurrentHashMap<Context, RefreshReceiver>()

        fun register(context: Context, callback: (Context?, Intent?) -> Unit) {
            if (receiverMap.containsKey(context)) {
                return
            }
            val receiver = RefreshReceiver(callback)
            receiverMap[context] = receiver
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(ACTION_REFRESH_WEBVIEW),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        fun unRegister(context: Context) {
            val receiver = receiverMap.remove(context)
            if (receiver != null) {
                context.unregisterReceiver(receiver)
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        callback(context, intent)
    }
}