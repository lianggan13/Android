package com.yunda.safe.plct.utility

import android.content.Context
import android.util.Log
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.Flattener2
import com.elvishew.xlog.libcat.LibCat
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.clean.NeverCleanStrategy
import com.elvishew.xlog.printer.file.naming.FileNameGenerator
import com.elvishew.xlog.printer.file.writer.SimpleWriter
import com.yunda.safe.plct.BuildConfig
import com.yunda.safe.plct.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

object Logger {

    fun init(content: Context) {
        val logDir = File(content.getExternalFilesDir(null), "logs")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }

        val MAX_TIME = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000 // 设置为7天前

        val filePrinter: Printer = FilePrinter.Builder(logDir.absolutePath)
            .fileNameGenerator(object : FileNameGenerator {
                private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

                override fun isFileNameChangeable(): Boolean {
                    return true
                }

                override fun generateFileName(logLevel: Int, timestamp: Long): String {
                    val currentDate = dateFormat.format(Date(timestamp))
                    return "$currentDate.log"
                }
            })
            .backupStrategy(NeverBackupStrategy()) // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
//            .backupStrategy(FileTimeBackupStrategy(24 * 60 * 60 * 1000)) // 每24小时备份一次
            .cleanStrategy(NeverCleanStrategy())
            .cleanStrategy(FileLastModifiedCleanStrategy(MAX_TIME))
            .flattener(object : Flattener2 {
                private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS")
                override fun flatten(
                    timeMillis: Long,
                    logLevel: Int,
                    tag: String?,
                    message: String?
                ): CharSequence {
                    val index = message?.indexOf('\n')!!
                    var threadName: String? = null
                    var log = message
                    if (index != -1) {
                        threadName = message.substring(0, index)
                        log = message.substring(index.plus(1))
                    }

                    val timestamp = dateFormat.format(Date(timeMillis))
                    val level = LogLevel.getShortLevelName(logLevel)
                    return "$timestamp|$threadName|$level|${log}"
                }
            })
            .writer(SimpleWriter()) // 指定日志写入器，默认为 SimpleWriter
            .build()

        val config = LogConfiguration.Builder()
            .logLevel(
                if (BuildConfig.DEBUG)
                    LogLevel.ALL
                else
                    LogLevel.NONE
            )
            .tag(content.getString(R.string.app_name))
            .enableThreadInfo()
            .build()

        val androidPrinter: Printer = object : AndroidPrinter(false) {
            override fun println(logLevel: Int, tag: String?, msg: String?) {
                val index = msg?.indexOf('\n')!!
                var threadName: String? = null
                var log = msg
                if (index != -1) {
                    threadName = msg.substring(0, index)
                    log = msg.substring(index.plus(1))
                }

                Log.println(logLevel, tag, "$threadName|${log}")
            }
        }

        LibCat.config(true, filePrinter)

        XLog.init(
            config,
            androidPrinter,
            filePrinter
        );
    }

}