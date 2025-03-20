package com.yunda.safe.plct.service

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ScheduledService {
    lateinit var scheduler: ScheduledExecutorService

    fun start(command: Runnable) {
        scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleWithFixedDelay(command, 5, 5, TimeUnit.SECONDS)
    }

    fun stop() {
        scheduler.shutdown()
    }
}