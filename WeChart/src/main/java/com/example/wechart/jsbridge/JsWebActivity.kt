package com.example.wechart.jsbridge;

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.wechart.R
import com.google.gson.Gson


class JsWebActivity : AppCompatActivity(), View.OnClickListener {
    private var TAG = "JsWebActivity";

    lateinit var webView: BridgeWebView

    lateinit var button: Button

    var RESULT_CODE: Int = 0

    var mUploadMessage: ValueCallback<Uri>? = null

    var mUploadMessageArray: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_js_web)

        webView = findViewById<View>(R.id.webView) as BridgeWebView

        button = findViewById<View>(R.id.button) as Button

        button.setOnClickListener(this)

        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.setWebChromeClient(object : WebChromeClient() {
            @Suppress("unused")
            fun openFileChooser(
                uploadMsg: ValueCallback<Uri>,
                AcceptType: String?,
                capture: String?
            ) {
                this.openFileChooser(uploadMsg)
            }

            @Suppress("unused")
            fun openFileChooser(uploadMsg: ValueCallback<Uri>, AcceptType: String?) {
                this.openFileChooser(uploadMsg)
            }

            fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                mUploadMessage = uploadMsg
                pickFile()
            }

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                mUploadMessageArray = filePathCallback
                pickFile()
                return true
            }
        })

        webView.addJavascriptInterface(
            MainJavascriptInterface(webView.getCallbacks(), webView),
            "WebViewJavascriptBridge"
        )
        webView.setGson(Gson())
        webView.loadUrl("file:///android_asset/demo.html")

        val user = User()
        val location = Location()
        location.address = "SDU"
        user.location = location
        user.name = "大头鬼"

        webView.callHandler("functionInJs", Gson().toJson(user), object : OnBridgeCallback {
            override fun onCallBack(data: String) {
                Log.d(TAG, "onCallBack: $data")
            }
        })

        webView.sendToWeb("hello")
    }

    fun pickFile() {
        val chooserIntent = Intent(Intent.ACTION_GET_CONTENT)
        chooserIntent.setType("image/*")
        startActivityForResult(chooserIntent, RESULT_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == RESULT_CODE) {
            if (null == mUploadMessage && null == mUploadMessageArray) {
                return
            }
            if (null != mUploadMessage && null == mUploadMessageArray) {
                val result = if (intent == null || resultCode != RESULT_OK) null else intent.data
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }

            if (null == mUploadMessage && null != mUploadMessageArray) {
                val result = if (intent == null || resultCode != RESULT_OK) null else intent.data
                if (result != null) {
                    mUploadMessageArray!!.onReceiveValue(arrayOf(result))
                }
                mUploadMessageArray = null
            }
        }
    }

    override fun onClick(v: View) {
        if (button == v) {
            webView.callHandler(
                "functionInJs", "data from Java"
            ) { data -> // TODO Auto-generated method stub
                Log.i(TAG, "reponse data from js $data")
            }
        }
    }

    class Location {
        var address: String? = null
    }

    class User {
        var name: String? = null
        var location: Location? = null
        var testStr: String? = null
    }
}