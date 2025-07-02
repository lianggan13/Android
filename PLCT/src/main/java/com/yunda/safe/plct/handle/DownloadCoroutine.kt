package com.yunda.safe.plct.handle

import com.elvishew.xlog.XLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

class DownloadCoroutine(
    private val listener: DownloadListener
) {
    companion object {
        const val TYPE_SUCCESS = 0
        const val TYPE_FAILED = 1
        const val TYPE_PAUSED = 2
        const val TYPE_CANCELED = 3
    }

    @Volatile
    private var isCanceled = false

    @Volatile
    private var isPaused = false

    private var lastProgress = 0

    private var job: Job? = null

    fun start(url: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val result = doDownload(url)
            withContext(Dispatchers.Main) {
                when (result) {
                    TYPE_SUCCESS -> listener.onSuccess()
                    TYPE_FAILED -> listener.onFailed()
                    TYPE_PAUSED -> listener.onPaused()
                    TYPE_CANCELED -> listener.onCanceled()
                }
            }
        }
    }

    fun pauseDownload() {
        isPaused = true
    }

    fun cancelDownload() {
        isCanceled = true
    }

    private suspend fun doDownload(downloadUrl: String): Int {
        var inputStream: InputStream? = null
        var savedFile: RandomAccessFile? = null
        var file: File? = null
        try {
            var downloadedLength: Long = 0
            val fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"))
            val directory = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            ).path
            file = File(directory + fileName)
            XLog.i("APK: $file")
            if (file.exists()) {
                downloadedLength = file.length()
                // TODO: for debug & test
                file.delete()
                downloadedLength = 0
            }

            val contentLength = getContentLength(downloadUrl)
            if (contentLength == 0L) {
                return TYPE_FAILED
            } else if (contentLength == downloadedLength) {
                return TYPE_SUCCESS
            } else if (contentLength < downloadedLength) {
                file.delete()
                downloadedLength = 0
            }
            val client = OkHttpClient()
            val request = Request.Builder()
                .addHeader("RANGE", "bytes=$downloadedLength-")
                .url(downloadUrl)
                .build()
            val response = client.newCall(request).execute()
            response.use { resp ->
                if (resp.isSuccessful) {
                    inputStream = resp.body?.byteStream()
                    savedFile = RandomAccessFile(file, "rw")
                    savedFile.seek(downloadedLength)
                    val buffer = ByteArray(1024)
                    var total = 0
                    var len: Int
                    var lastLogProgress = 0
                    while (inputStream?.read(buffer).also { len = it ?: -1 } != -1) {
                        if (isCanceled) return TYPE_CANCELED
                        if (isPaused) return TYPE_PAUSED
                        total += len
                        savedFile.write(buffer, 0, len)
                        val progress = ((total + downloadedLength) * 100 / contentLength).toInt()
                        if (progress > lastProgress) {
                            lastProgress = progress
                            withContext(Dispatchers.Main) { listener.onProgress(progress) }
                        }
                        if (progress / 10 > lastLogProgress / 10) {
                            lastLogProgress = progress
                            XLog.tag("DownloadCoroutine").i(
                                "Download: $total, Exists: $downloadedLength, Total: $contentLength, Progress: $progress%"
                            )
                        }
                    }
                    return TYPE_SUCCESS
                }
            }
        } catch (e: Exception) {
            XLog.e(e.message, e)
        } finally {
            try {
                inputStream?.close()
                savedFile?.close()
                if (isCanceled && file != null) {
                    file.delete()
                }
            } catch (e: Exception) {
                XLog.e(e.message, e)
            }
        }
        return TYPE_FAILED
    }

    private fun getContentLength(downloadUrl: String): Long {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(downloadUrl)
            .build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val contentLengthStr = response.header("ContentLength")
                return contentLengthStr?.toLongOrNull() ?: 0L
            }
        }
        return 0
    }
}