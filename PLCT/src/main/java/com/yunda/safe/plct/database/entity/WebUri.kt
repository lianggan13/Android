package com.yunda.safe.plct.database.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "WebUri")
data class WebUri(
    @PrimaryKey
    @NonNull
    val id: UUID = UUID.randomUUID(),
    var uri: String = "",
    var date: Date = Date(),
) {
    //    @Ignore
//    val photoFileName = "IMG_{$id}.jpg"
//
    @Ignore
    constructor() : this(UUID.randomUUID())
}
