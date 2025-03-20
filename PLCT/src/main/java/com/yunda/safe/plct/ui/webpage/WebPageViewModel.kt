package com.yunda.safe.plct.ui.webpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yunda.safe.plct.common.StringConstants
import com.yunda.safe.plct.database.AppRepository
import com.yunda.safe.plct.database.entity.WebUri
import com.yunda.safe.plct.utility.Preferences

class WebPageViewModel : ViewModel() {
    private val appRepository = AppRepository.get()
    val mWebUrisLiveData = appRepository.getWebUris()

    private val _uri = MutableLiveData<String>().apply {
        value = Preferences.getString(StringConstants.WEB_URI, "http://")
    }
    val uri: LiveData<String> = _uri

    fun setUri(newUri: String) {
        if (_uri.value != newUri) {
            _uri.value = newUri
            Preferences.saveString(StringConstants.WEB_URI, newUri)
        }
    }

    fun addWebUri(uri: WebUri) {
        appRepository.addWebUri(uri)
    }

    fun deleteWebUri(uri: WebUri) {
        appRepository.deleteWebUri(uri)
    }

    fun GetWebUri(itemToRemove: String): WebUri? {
        val webUri = mWebUrisLiveData.value?.firstOrNull() { it.uri == itemToRemove }
        return webUri
    }

    override fun onCleared() {
        super.onCleared()
    }

}