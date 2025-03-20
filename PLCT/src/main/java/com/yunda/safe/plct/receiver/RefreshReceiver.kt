package com.yunda.safe.plct.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.yunda.safe.plct.work.PollWorker.Companion.ACTION_REFRESH_WEBVIEW

class RefreshReceiver(private val callback: (Context?, Intent?) -> Unit) : BroadcastReceiver() {

    companion object {
        private lateinit var receiver: RefreshReceiver

        fun register(context: Context, callback: (Context?, Intent?) -> Unit) {
            receiver = RefreshReceiver(callback)
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(ACTION_REFRESH_WEBVIEW),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        fun unRegister(context: Context) {
            context.unregisterReceiver(receiver)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        callback(context, intent)
    }
}