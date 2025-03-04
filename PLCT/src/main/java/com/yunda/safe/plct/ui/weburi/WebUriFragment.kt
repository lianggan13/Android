package com.yunda.safe.plct.ui.weburi

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yunda.safe.plct.R
import com.yunda.safe.plct.adapter.WebUriAdapter
import com.yunda.safe.plct.common.StringConstants
import com.yunda.safe.plct.database.entity.WebUri
import com.yunda.safe.plct.databinding.FragmentWebUriBinding

private val TAG = "WebUriFragment"

class WebUriFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var searchBox: AutoCompleteTextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var popupWindow: PopupWindow

    private val viewModel: WebUriViewModel by viewModels()
    private var _binding: FragmentWebUriBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebUriBinding.inflate(layoutInflater)

        val root: View = binding.root
        searchBox = binding.searchBox
//        recyclerView = binding.historyRecyclerView
        spinner = binding.spinner

        recyclerView = RecyclerView(requireContext()).apply {

        }

        // 创建数据源
        val items = arrayOf("选项 1", "选项 2", "选项 3", "选项 4")

        // 创建适配器
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)

        // 设置下拉视图的布局
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 将适配器应用到 Spinner
        spinner.adapter = adapter

        // 设置选择监听器
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                // 处理选择的项
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 没有选择项的处理
            }
        }

        // 监听搜索框的输入事件
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                updateUI(viewModel.mWebUrisLiveData.value ?: emptyList(), query)
            }
        })


        // 确认搜索并添加到历史记录
        searchBox.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val searchText = searchBox.text.toString()
                if (searchText.isNotEmpty()) {
                    addHistoryItem(searchText)
                }
                searchBox.text.clear() // 清空输入框
                recyclerView.visibility = View.GONE
                true
            } else {
                false
            }
        }

        // 创建 PopupWindow
        popupWindow = PopupWindow(recyclerView, ViewGroup.LayoutParams.MATCH_PARENT, 400, true)
        popupWindow.isFocusable = false
        popupWindow.isOutsideTouchable = true

        return root

        val txtUri = binding.txtUri
        viewModel.uri.observe(viewLifecycleOwner) {
            txtUri.setText(it)
        }

        binding.btnNav.setOnClickListener {
            var uri = txtUri.text.toString()
            viewModel.setUri(uri)
            val webUri = WebUri().apply { this.uri = uri }
            viewModel.addWebUri(webUri)

            //  toWebPage(uri)
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.mWebUrisLiveData.observe(viewLifecycleOwner) { webUris ->
            webUris?.let {
                updateUI(it, searchBox.text.toString())
            }
        }
    }

    private fun updateUI(webUris: List<WebUri>, query: String) {
        var uris = webUris.map { it.uri }
        val filteredList = uris.filter { it.contains(query, ignoreCase = true) }

        setupHistoryAdapter(filteredList.toMutableList())
//        historyAdapter.submitList(uris)
        recyclerView.visibility = if (filteredList.isNotEmpty()) View.VISIBLE else View.GONE

        if (filteredList.isNotEmpty()) {
            if (!popupWindow.isShowing) {
                popupWindow.showAsDropDown(searchBox)
            }
        } else {
            popupWindow.dismiss()
        }
    }

    // 模拟添加历史记录
    private fun addHistoryItem(uri: String) {
        val uris = viewModel.mWebUrisLiveData.value?.map { it.uri } ?: emptyList()
        if (uri !in uris) {
            val webUri = WebUri()
            webUri.uri = uri
            viewModel.addWebUri(webUri)
        }
    }


    private fun toWebPage(uriStr: String) {
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.webUriFragment) {
            return
        }

        val uri = Uri.parse(uriStr)
        val bundle = Bundle().apply {
            putParcelable(StringConstants.WEB_URI, uri)
        }

        navController.navigate(R.id.action_webUri_to_webPage, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}