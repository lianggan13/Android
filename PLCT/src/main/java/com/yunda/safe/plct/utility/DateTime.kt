package com.yunda.safe.plct.utility

import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Date


object DateTime {

    fun parseTime(timeString: String, pattern: String = "HH:mm:ss"): LocalTime {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return LocalTime.parse(timeString, formatter)
    }

    fun parseDateTime(
        dateTimeString: String,
        pattern: String = "yyyy-MM-dd HH:mm:ss"
    ): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return LocalDateTime.parse(dateTimeString, formatter)
    }

    fun setSysDateTime(
        date: Date,
    ): Int {

        // 格式化为 date 命令需要的格式
        val dateCmdFormat = java.text.SimpleDateFormat("MMddHHmmyyyy.ss")
        val cmdTime = dateCmdFormat.format(date)

        // 构造命令
        val cmd = arrayOf("su", "-c", "date $cmdTime")

        // 执行命令
        val process = Runtime.getRuntime().exec(cmd)
        val exitCode = process.waitFor()

        return exitCode;
    }
}