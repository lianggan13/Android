package com.yunda.safe.plct.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yunda.safe.plct.database.entity.WebUri
import java.util.UUID

@Dao
interface WebUriDao {
    @Query("select * from WebUri")
    fun getWebUris(): LiveData<List<WebUri>>

    @Query("select * from WebUri where id = (:id)")
    fun getWebUri(id: UUID): LiveData<WebUri?>

    @Update
    fun updateWebUri(webUri: WebUri)

    @Insert
    fun insertWebUri(webUri: WebUri)

//    @Query("select * from WebUri where id = (:id)")
//     fun queryWebUri(id: UUID): WebUri?

    @Delete
    fun deleteWebUri(webUri: WebUri)

}