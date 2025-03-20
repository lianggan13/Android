package com.yunda.safe.plct

import android.app.Application
import com.yunda.safe.plct.database.AppRepository
import com.yunda.safe.plct.utility.Logger
import com.yunda.safe.plct.utility.Preferences


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Preferences.init(this@App)
        AppRepository.initialize(this@App)

//        PollWorker.cancel(this@App)

        Logger.init(this@App)
    }

    override fun onTerminate() {
        super.onTerminate()
        AppRepository.get().close()
    }
}