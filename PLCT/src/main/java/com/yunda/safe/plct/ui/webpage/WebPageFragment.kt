package com.yunda.safe.plct.ui.webpage

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yunda.safe.plct.R
import com.yunda.safe.plct.common.StringConstants
import com.yunda.safe.plct.databinding.FragmentWebPageBinding
import com.yunda.safe.plct.utility.AppPreferences

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

    private lateinit var mUri: Uri
    private var _binding: FragmentWebPageBinding? = null;
    private lateinit var mWebView: WebView
    private var mMenuProgressBar: MenuItem? = null;


    private val viewModel: WebPageViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mUri = Uri.parse(arguments?.getString(ARG_URI)?:"");
        mUri = arguments?.getParcelable(StringConstants.WEB_URI) ?: Uri.EMPTY
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_web_page, menu)
                val searchItem: MenuItem = menu.findItem(R.id.menu_item_uri)
                val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
                searchView.apply {
                    setOnQueryTextListener(object :
                        androidx.appcompat.widget.SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(queryText: String): Boolean {
                            Log.d(TAG, "QueryTextSubmit: $queryText")
//                            mPhotoGalleryViewModel.fetchPhotos(queryText, mSearchType)


                            //26.6 优化搜索栏
                            //关闭软键盘的方式1：失去焦点
//                            searchView.clearFocus()
                            //关闭软键盘的方式2：调用输入法接口
                            //hideKeyboard()

                            searchView.onActionViewCollapsed()

//                            resetRecyclerViewAdapter()
                            showLoadingProgress()

                            mUri = Uri.parse(queryText)
                            AppPreferences.saveString(StringConstants.WEB_URI, mUri.toString())

                            mWebView.loadUrl(mUri.toString())
                            return true
                        }

                        override fun onQueryTextChange(queryText: String): Boolean {
                            Log.d(TAG, "QueryTextChange: $queryText")
                            return false
                        }
                    })

                    //点击搜索按钮展开SearchView时
                    setOnSearchClickListener {
                        //26.4 获取上次保存的值，同时不提交查询：只显示值
                        searchView.setQuery(mUri.toString(), false)
                    }

                    setOnCloseListener {
                        //必须放回false(也是默认值），否则会出现无法关闭SearchView
                        return@setOnCloseListener false
                    }
                }

                mMenuProgressBar = menu.findItem(R.id.menu_item_progress)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_item_uri -> {
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner) // 使用 Fragment 的生命周期


        val progressBar = _binding?.progressHorizontal!!
        mWebView = _binding?.webView!!
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
                hideLoadingProgress()
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

        mWebView.loadUrl(mUri.toString())
    }

    /**
     * 显示标题栏进度指示器
     */
    private fun showLoadingProgress() {
        mMenuProgressBar?.isVisible = true

    }

    /**
     * 隐藏标题栏进度指示器
     */
    private fun hideLoadingProgress() {
        mMenuProgressBar?.isVisible = false
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
    }
}