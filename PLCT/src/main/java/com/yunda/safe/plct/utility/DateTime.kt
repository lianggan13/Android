package com.yunda.safe.plct.utility

import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

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

//    fun convertThreeTenToJava(threeTenLocalTime: LocalTime): JavaLocalTime {
//        return JavaLocalTime.from(threeTenLocalTime)
//    }


//    fun parseTime(timeString: String, pattern: String = "HH:mm:ss"): LocalTime {
//        val formatter = DateTimeFormatter.ofPattern(pattern)
//        return LocalTime.parse(timeString, formatter)
//    }
//
//    fun parseDateTime(
//        dateTimeString: String,
//        pattern: String = "yyyy-MM-dd HH:mm:ss"
//    ): LocalDateTime {
//        val formatter = DateTimeFormatter.ofPattern(pattern)
//        return LocalDateTime.parse(dateTimeString, formatter)
//    }
}