package com.yunda.safe.plct.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yunda.safe.plct.database.convert.DbTypeConverters
import com.yunda.safe.plct.database.dao.WebUriDao
import com.yunda.safe.plct.database.entity.WebUri

@Database(entities = [WebUri::class], version = 1, exportSchema = false)
@TypeConverters(DbTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun webUriDao(): WebUriDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app.db"
                    )
                        // .addMigrations(migration_1_2)
                        // .addMigrations(migration_2_3)
                        .build()
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    // 处理异常，记录错误或重新抛出
                    e.printStackTrace() // 打印异常堆栈
                    throw RuntimeException("Unable to create database", e) // 重新抛出异常
                }
            }
        }
    }
}