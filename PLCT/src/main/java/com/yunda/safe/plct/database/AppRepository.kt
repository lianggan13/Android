package com.yunda.safe.plct.database


import android.content.Context
import androidx.lifecycle.LiveData
import com.yunda.safe.plct.database.entity.WebUri
import java.util.concurrent.Executors


class AppRepository(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: AppRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) { // 双重检查
                        INSTANCE = AppRepository(context)
                    }
                }
            }
        }

        fun getRepository(context: Context): AppRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppRepository(context).also { INSTANCE = it }
            }
        }

        fun get(): AppRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }

    private val mExecutor = Executors.newSingleThreadExecutor()

    private val appDatabase: AppDatabase = AppDatabase.getDatabase(context)

    private val webUriDao = appDatabase.webUriDao()

    fun getWebUris(): LiveData<List<WebUri>> = webUriDao.getWebUris()

    fun addWebUri(webUri: WebUri) {
        mExecutor.execute {
            webUriDao.insertWebUri(webUri)
        }
    }

    fun deleteWebUri(webUri: WebUri) {
        mExecutor.execute {
            webUriDao.deleteWebUri(webUri)
        }
    }

    fun close() {
//        if (::appDatabase.isInitialized) {
//            appDatabase.close() // 手动关闭数据库
//        }
        appDatabase.close()
    }
}
