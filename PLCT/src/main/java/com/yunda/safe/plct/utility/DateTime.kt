package com.yunda.safe.plct.utility

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
}