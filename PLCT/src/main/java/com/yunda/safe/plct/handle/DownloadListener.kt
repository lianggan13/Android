package com.yunda.safe.plct.handle

interface DownloadListener {

    fun onProgress(progress: Int)

    fun onSuccess()

    fun onFailed()

    fun onPaused()

    fun onCanceled()
}