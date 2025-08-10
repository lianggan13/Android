package com.yunda.safe.plct.data

data class ApkVersion(
    val id: String,
    val versionNo: String,
    val versionNumber: String,
    val createTime: String,
    val filePath: String,
    val isForceUpdate: Boolean,
    val desc: String,
    val appType: Int
) : java.io.Serializable
