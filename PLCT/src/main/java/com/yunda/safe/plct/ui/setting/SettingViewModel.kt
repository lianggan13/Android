package com.yunda.safe.plct.ui.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elvishew.xlog.XLog
import com.yunda.safe.plct.utility.BrowserLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel : ViewModel() {

    private val _isOnline = MutableLiveData<Boolean>().apply { value = false }
    val IsOnline: MutableLiveData<Boolean> = _isOnline

    private var checkJob: Job? = null

    /**
     * 启动周期性在线检查（在 ViewModel scope 内运行）
     */
    fun startCheckOnline(
        urls: Array<String?>,
        intervalMs: Long = 5_000L,
        stopWhenOnline: Boolean = true
    ) {
        if (urls.isEmpty()) {
            XLog.w("[SettingViewModel] startCheckOnline: hosts array is empty")
            return
        }
        if (checkJob?.isActive == true) {
            XLog.i("[SettingViewModel] startCheckOnline: already running")
            return
        }

        checkJob = viewModelScope.launch(Dispatchers.IO) {
            XLog.i("[SettingViewModel] Online check started (hosts=${urls.joinToString()}, interval=${intervalMs}ms)")
            while (isActive) {
                var allOnline = true
                var lastStatus = -3
                for (host in urls) {
                    if (host == null || host.isEmpty())
                        continue

                    val status = try {
                        BrowserLauncher.getHttpStatus(host)
                    } catch (e: Exception) {
                        XLog.w("[SettingViewModel] check host failed: $host, ${e.message}", e)
                        -3
                    }
                    lastStatus = status
                    XLog.i("[SettingViewModel] Checked $host -> $status")
                    if (status != 200) {
                        allOnline = false
                        break
                    }
                }

                withContext(Dispatchers.Main) {
                    setIsOnline(allOnline)
                }

                if (allOnline) {
                    XLog.i("[SettingViewModel] All hosts are online (lastStatus=$lastStatus)")
                    if (stopWhenOnline) break
                } else {
                    XLog.i("[SettingViewModel] Not all hosts online (lastStatus=$lastStatus), will retry")
                }

                delay(intervalMs)
            }
            XLog.i("[SettingViewModel] Online check stopped")
        }
    }

    fun stopCheckOnline() {
        try {
            checkJob?.cancel()
            checkJob = null
            XLog.i("[SettingViewModel] stopCheckOnline called")
        } catch (e: Exception) {
            XLog.w("[SettingViewModel] stopCheckOnline failed: ${e.message}", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopCheckOnline()
    }

    fun setIsOnline(state: Boolean) {
        if (_isOnline.value != state) {
            // _isOnline.value = state
            _isOnline.postValue(state)
        }
    }
}