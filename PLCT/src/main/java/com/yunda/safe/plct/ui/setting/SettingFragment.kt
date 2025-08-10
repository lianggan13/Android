package com.yunda.safe.plct.ui.setting

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.elvishew.xlog.XLog
import com.yunda.safe.plct.common.ACTION_SHOW_SHOW_NOTIFICATION
import com.yunda.safe.plct.common.APK_VERSION
import com.yunda.safe.plct.common.BROWSER_HOMEPAGE
import com.yunda.safe.plct.common.DEFAULT_BROWSER_HOMEPAGE
import com.yunda.safe.plct.common.DEFAULT_SERVER_HOST
import com.yunda.safe.plct.common.DEFAULT_SOFTWARE_VERSION
import com.yunda.safe.plct.common.PERMISSION_PRIVATE
import com.yunda.safe.plct.common.SERVER_HOST
import com.yunda.safe.plct.data.ApkVersion
import com.yunda.safe.plct.databinding.FragmentSettingBinding
import com.yunda.safe.plct.receiver.RefreshReceiver
import com.yunda.safe.plct.service.DownloadService
import com.yunda.safe.plct.utility.BrowserLauncher
import com.yunda.safe.plct.utility.Preferences


class SettingFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModels()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    // 迁移的组件
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val apkVersion = intent.getSerializableExtra(APK_VERSION) as? ApkVersion
            Toast.makeText(
                requireContext(),
                "Received broadcast: ${intent.action}, version: ${apkVersion?.versionNo}",
                Toast.LENGTH_LONG
            ).show()

            AlertDialog.Builder(context!!)
                .setTitle("版本更新")
                .setMessage("检测到新版本，是否立即更新？")
                .setPositiveButton("更新") { _, _ ->
                    val url = apkVersion?.filePath
                    downloadBinder?.startDownload(url)
                }
                .setNegativeButton("取消") { _, _ ->
                    // 取消操作
                }
                .setCancelable(false)
                .show()

            XLog.i("Received broadcast: ${intent.action}, version: ${apkVersion?.versionNo}")
        }
    }

    private val mFilter: IntentFilter = IntentFilter(ACTION_SHOW_SHOW_NOTIFICATION)

    private val mLifecycleObserver = object : DefaultLifecycleObserver {

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)

            requireActivity().registerReceiver(
                mReceiver,
                mFilter,
                PERMISSION_PRIVATE,
                null
            )

            RefreshReceiver.register(requireActivity()) { context, intent ->
                requireActivity().runOnUiThread {
                    XLog.i("SettingFragment: Refresh signal received")
                    // 在设置页面可以刷新配置或重新加载设置
                    loadSettings()
                }
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)

            requireActivity().unregisterReceiver(mReceiver)
            RefreshReceiver.unRegister(requireActivity())
        }
    }

    private var downloadBinder: DownloadService.DownloadBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadBinder = service as DownloadService.DownloadBinder
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 添加生命周期观察者
        lifecycle.addObserver(mLifecycleObserver)

        // 启动和绑定下载服务
        val activity = requireActivity()
        val intent = Intent(activity, DownloadService::class.java)
        activity.startService(intent) // 启动服务：保证 Service 一直在后台运行
        activity.bindService(
            intent,
            connection,
            BIND_AUTO_CREATE
        ) // 绑定服务：让 Activity 与 Service 进行通信

        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        loadSettings()
    }

    private fun setupClickListeners() {
        // 保存按钮点击事件
        binding.btnSave.setOnClickListener {
            saveSettings()
        }

        // 重置按钮点击事件
        binding.btnReset.setOnClickListener {
            resetSettings()
        }
    }

    private fun loadSettings() {
        // 从 SharedPreferences 加载设置
        val softwareVersion = Preferences.getString(APK_VERSION, DEFAULT_SOFTWARE_VERSION)
        val serverHost = Preferences.getString(SERVER_HOST, DEFAULT_SERVER_HOST)
        val browserHomepage = Preferences.getString(BROWSER_HOMEPAGE, DEFAULT_BROWSER_HOMEPAGE)

        // 设置到输入框
        binding.etSoftwareVersion.setText(softwareVersion)
        binding.etServerHost.setText(serverHost)
        binding.etBrowserHomepage.setText(browserHomepage)

        // 检查浏览器主页格式，如果格式正确则直接启动浏览器
        if (browserHomepage != null && browserHomepage.isNotEmpty() &&
            (browserHomepage.startsWith("http://") || browserHomepage.startsWith("https://"))
        ) {
            XLog.i("Browser homepage loaded from settings: $browserHomepage")
            
            return

            BrowserLauncher.waitForWebsiteAndLaunch(
                context = requireContext(),
                url = browserHomepage,
                showToast = true
            )
        } else {
            XLog.w("Invalid browser homepage format in settings: $browserHomepage")
        }
    }

    private fun saveSettings() {
        val softwareVersion = binding.etSoftwareVersion.text.toString().trim()
        val serverHost = binding.etServerHost.text.toString().trim()
        val browserHomepage = binding.etBrowserHomepage.text.toString().trim()

        // 验证输入
        if (softwareVersion.isEmpty()) {
            Toast.makeText(requireContext(), "请输入软件版本号", Toast.LENGTH_SHORT).show()
            return
        }

        if (serverHost.isEmpty()) {
            Toast.makeText(requireContext(), "请输入服务器地址", Toast.LENGTH_SHORT).show()
            return
        }

        if (browserHomepage.isEmpty()) {
            Toast.makeText(requireContext(), "请输入浏览器主页地址", Toast.LENGTH_SHORT).show()
            return
        }

        // 验证服务器地址格式
        if (!serverHost.startsWith("http://") && !serverHost.startsWith("https://")) {
            Toast.makeText(
                requireContext(),
                "服务器地址格式不正确，请以 http:// 或 https:// 开头",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // 验证浏览器主页地址格式
        if (!browserHomepage.startsWith("http://") && !browserHomepage.startsWith("https://")) {
            Toast.makeText(
                requireContext(),
                "浏览器主页地址格式不正确，请以 http:// 或 https:// 开头",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // 保存到 SharedPreferences
        Preferences.saveString(APK_VERSION, softwareVersion)
        Preferences.saveString(SERVER_HOST, serverHost)
        Preferences.saveString(BROWSER_HOMEPAGE, browserHomepage)

        // 使用工具类启动浏览器
        BrowserLauncher.waitForWebsiteAndLaunch(
            context = requireContext(),
            url = browserHomepage,
            showToast = true
        )

        Toast.makeText(requireContext(), "设置已保存", Toast.LENGTH_SHORT).show()
    }

    private fun resetSettings() {
        // 重置为默认值
//        binding.etSoftwareVersion.setText(DEFAULT_SOFTWARE_VERSION)
        binding.etServerHost.setText(DEFAULT_SERVER_HOST)
        binding.etBrowserHomepage.setText(DEFAULT_BROWSER_HOMEPAGE)

        Toast.makeText(requireContext(), "已重置为默认值", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        lifecycle.removeObserver(mLifecycleObserver)

        // 解绑服务
        try {
            requireActivity().unbindService(connection)
        } catch (e: Exception) {
            XLog.w("Failed to unbind service: ${e.message}")
        }
    }
}