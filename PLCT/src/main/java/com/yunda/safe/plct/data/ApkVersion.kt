package com.yunda.safe.plct.data

data class ApkVersion(
    val apk: String,
    val version: String,
    val success: Boolean
) : java.io.Serializable
