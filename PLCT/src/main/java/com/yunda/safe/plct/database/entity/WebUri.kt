package com.yunda.safe.plct.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "WebUri")
data class WebUri(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var uri: String = "",
    var date: Date = Date(),
) {
    @Ignore
    constructor() : this(UUID.randomUUID())
}