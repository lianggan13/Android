package com.yunda.safe.plct.ui.webpage

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AutoCompleteTextView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yunda.safe.plct.R
import com.yunda.safe.plct.adapter.WebUriAdapter
import com.yunda.safe.plct.common.StringConstants
import com.yunda.safe.plct.database.entity.WebUri
import com.yunda.safe.plct.databinding.FragmentWebPageBinding
import com.yunda.safe.plct.utility.Keyboard
import com.yunda.safe.plct.work.PollWorker.Companion.ACTION_REFRESH_WEBVIEW

class WebPageFragment : Fragment() {

    companion object {
        const val TAG = "WebPageFragment"
        fun newInstance(uri: Uri): WebPageFragment {
            return WebPageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(StringConstants.WEB_URI, uri)
                }
            }
        }
    }

    private var _binding: FragmentWebPageBinding? = null;

    private lateinit var searchBox: AutoCompleteTextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var popupWindow: PopupWindow
    private lateinit var mWebView: WebView

    private val viewModel: WebPageViewModel by viewModels()

    private val binding get() = _binding!!

    private val mLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)

            ContextCompat.registerReceiver(
                requireActivity(),
                refreshReceiver,
                IntentFilter(ACTION_REFRESH_WEBVIEW),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            requireActivity().unregisterReceiver(refreshReceiver)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var uri = arguments?.getParcelable(StringConstants.WEB_URI) ?: Uri.EMPTY
        lifecycle.addObserver(mLifecycleObserver)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!binding.linearSearch.isVisible) {
                        binding.linearSearch.visibility = View.VISIBLE;
                    } else {
                        isEnabled = false // 取消回调
                        requireActivity().onBackPressedDispatcher.onBackPressed() // 触发默认回退
                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        searchBox = binding.searchBox
        searchBox.setText(viewModel.uri.value)
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                updateUI(viewModel.mWebUrisLiveData.value ?: emptyList(), query)
            }
        })

        searchBox.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                commitUri()
                true
            } else {
                false
            }
        }

        binding.btnSearch.setOnClickListener {
            commitUri()
        }

        recyclerView = RecyclerView(requireContext()).apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        popupWindow =
            PopupWindow(recyclerView, ViewGroup.LayoutParams.MATCH_PARENT, 400, true).apply {
                isFocusable = false
                isOutsideTouchable = true
            }

        initWebView()

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uri.observe(viewLifecycleOwner) {
            mWebView.loadUrl(viewModel.uri.value.toString())
        }

        viewModel.mWebUrisLiveData.observe(viewLifecycleOwner) { webUris ->
            webUris?.let {
                updateUI(it, searchBox.text.toString())
            }
        }
    }

    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mWebView.reload()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val progressBar = binding.progressHorizontal
        mWebView = binding.webView
        mWebView.settings.javaScriptEnabled = true
        mWebView.webViewClient = object : WebViewClient() {
            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()  // ignore SSL error
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.e("WebView", "${error?.description}")
                Toast.makeText(context, "网页加载错误: ${error?.description}", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.progress = newProgress
                    progressBar.visibility = View.VISIBLE
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                val appCompatActivity = requireActivity() as AppCompatActivity
                appCompatActivity.supportActionBar?.subtitle = title
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                return super.onConsoleMessage(consoleMessage)
            }
        }
    }

    private fun setupHistoryAdapter(historyList: MutableList<String>) {
        // 设置适配器
        val webUriAdapter = WebUriAdapter(historyList,
            // 处理点击事件
            { selectedItem ->
                searchBox.setText(selectedItem)
                recyclerView.visibility = View.GONE
                popupWindow.dismiss()
            },
            // 移除历史记录
            { itemToRemove ->
                val webUri = viewModel.GetWebUri(itemToRemove)
                if (webUri != null)
                    viewModel.deleteWebUri(webUri)
            }
        )

        if (recyclerView.layoutManager == null)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = webUriAdapter
    }

    private fun commitUri() {
        val uri = searchBox.text.toString()

        searchBox.clearFocus()
        Keyboard.hideKeyboard(requireActivity(), requireContext())

        if (uri.isNotEmpty()) {
            viewModel.setUri(uri)

            recyclerView.visibility = View.GONE
            binding.linearSearch.visibility = View.GONE
            addHistoryUri(uri)
        }
    }

    private fun updateUI(webUris: List<WebUri>, query: String) {
        var uris = webUris.map { it.uri }
        val filteredList = uris.filter { it.contains(query, ignoreCase = true) }

        setupHistoryAdapter(filteredList.toMutableList())

        if (binding.linearSearch.visibility == View.VISIBLE && filteredList.isNotEmpty()) {
            if (!popupWindow.isShowing) {
                recyclerView.visibility = View.VISIBLE
                popupWindow.showAsDropDown(searchBox)
            }
        } else {
            recyclerView.visibility = View.GONE
            popupWindow.dismiss()
        }
    }

    private fun addHistoryUri(uri: String) {
        val uris = viewModel.mWebUrisLiveData.value?.map { it.uri } ?: emptyList()

        if (uri !in uris) {
            viewModel.addWebUri(WebUri(uri = uri))
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        outState.putParcelable(StringConstants.WEB_URI, mUri)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        lifecycle.removeObserver(mLifecycleObserver)
    }
}