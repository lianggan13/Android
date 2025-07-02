package com.yunda.safe.plct.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.elvishew.xlog.XLog
import com.yunda.safe.plct.MainActivity

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "System boot completed", Toast.LENGTH_LONG).show()
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            XLog.i("System boot completed")

            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}



