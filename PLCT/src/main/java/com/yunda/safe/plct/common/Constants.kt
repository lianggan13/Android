package com.yunda.safe.plct.common

import com.yunda.safe.plct.BuildConfig
import com.yunda.safe.plct.utility.Preferences


object Constants {
    // fun Host(): String? {
    //     val host = Preferences.getString(
    //         SERVER_HOST,
    //         DEFAULT_SERVER_HOST
    //     )
    //     return host;
    // }

    const val WEB_URI = "WEB_URI"

    const val ACTION_REFRESH_WEBVIEW = BuildConfig.APPLICATION_ID + ".REFRESH_WEBVIEW"
    const val ACTION_SHOW_SHOW_NOTIFICATION = BuildConfig.APPLICATION_ID + ".SHOW_NOTIFICATION"
    const val PERMISSION_PRIVATE = BuildConfig.APPLICATION_ID + ".PRIVATE"

    const val APK_VERSION = "APK_VERSION"
    const val DEFAULT_SOFTWARE_VERSION = "0.0.0.0"

    const val SERVER_HOST = "SERVER_HOST"
    const val DEFAULT_SERVER_HOST = "http://10.60.0.66:9291"

    const val BROWSER_HOMEPAGE = "BROWSER_HOMEPAGE"
    const val DEFAULT_BROWSER_HOMEPAGE = "http://10.60.0.66:9291/#/terminal/3"

    var Host: String?
        get() = Preferences.getString(
            SERVER_HOST,
            DEFAULT_SERVER_HOST
        )
        set(value) {
            Preferences.saveString(SERVER_HOST, value ?: DEFAULT_SERVER_HOST)
        }

    var Version: String?
        get() = Preferences.getString(
            APK_VERSION,
            DEFAULT_SOFTWARE_VERSION
        )
        set(value) {
            Preferences.saveString(APK_VERSION, value ?: DEFAULT_SOFTWARE_VERSION)
        }
}