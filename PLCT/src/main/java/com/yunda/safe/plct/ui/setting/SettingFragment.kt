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
import com.yunda.safe.plct.api.API
import com.yunda.safe.plct.api.ApiClient
import com.yunda.safe.plct.common.Constants
import com.yunda.safe.plct.data.ApkVersion
import com.yunda.safe.plct.databinding.FragmentSettingBinding
import com.yunda.safe.plct.receiver.RefreshReceiver
import com.yunda.safe.plct.service.AlarmService
import com.yunda.safe.plct.service.DownloadService
import com.yunda.safe.plct.utility.BrowserLauncher
import com.yunda.safe.plct.utility.Preferences


class SettingFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModels()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val versionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val apkVersion = intent.getSerializableExtra(Constants.APK_VERSION) as? ApkVersion
            Toast.makeText(
                requireContext(),
                "Received broadcast: ${intent.action}, version: ${apkVersion?.versionNo}",
                Toast.LENGTH_LONG
            ).show()

            AlertDialog.Builder(context!!)
                .setTitle("版本更新")
                .setMessage("检测到新版本 ${apkVersion?.versionNo}，是否立即更新？")
                .setPositiveButton("更新") { _, _ ->
                    val url = apkVersion?.filePath
                    downloadBinder?.startDownload(url)
                }
                .setNegativeButton("取消") { _, _ ->

                }
                .setCancelable(false)
                .show()

            XLog.i("Received broadcast: ${intent.action}, version: ${apkVersion?.versionNo}")
        }
    }

    private val mFilter: IntentFilter = IntentFilter(Constants.ACTION_SHOW_SHOW_NOTIFICATION)

    private val mLifecycleObserver = object : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)

            requireActivity().registerReceiver(
                versionReceiver,
                mFilter,
                Constants.PERMISSION_PRIVATE,
                null
            )

            RefreshReceiver.register(requireActivity()) { context, intent ->
                requireActivity().runOnUiThread {
                    XLog.i("SettingFragment: Refresh signal received")
                    launchWebsite()
                    timeoutReload()
                }
            }
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            requireActivity().unregisterReceiver(versionReceiver)
            RefreshReceiver.unRegister(requireActivity())
            AlarmService.cancelAlarm(requireActivity())
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

        viewModel.IsOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline == true) {
                // call waitForWebsiteAndLaunch
                launchWebsite()
                // call timeoutReload
                timeoutReload()
            } else {
                // start check online thread
                viewModel.startCheckOnline(arrayOf(Constants.Host, Constants.HomePage))
            }
        }

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

    private fun launchWebsite() {
        // val browserHomepage = binding.etBrowserHomepage.text.toString()
        val browserHomepage =
            Preferences.getString(Constants.BROWSER_HOMEPAGE, Constants.DEFAULT_BROWSER_HOMEPAGE)
        // 检查浏览器主页格式，如果格式正确则直接启动浏览器
        if (browserHomepage != null && browserHomepage.isNotEmpty() &&
            (browserHomepage.startsWith("http://") || browserHomepage.startsWith("https://"))
        ) {
            XLog.i("Browser homepage loaded from settings: $browserHomepage")
            BrowserLauncher.waitForWebsiteAndLaunch(
                context = requireContext(),
                url = browserHomepage
            )
        } else {
            XLog.w("Invalid browser homepage format in settings: $browserHomepage")
        }
    }

    private fun timeoutReload() {
        val url = "${Constants.Host}${API.SYSTEM_RST}"
        ApiClient.postAsync(url, null, 10) { resp, error ->
            if (error != null) {
                XLog.e("GET failed", error)
                viewModel.setIsOnline(false)
                return@postAsync
            }
            if (resp == null || !resp.isSuccessful) {
                XLog.e("GetByCode failed: ${resp?.code} ${resp?.message}")
                viewModel.setIsOnline(false)
                return@postAsync
            }

            try {
                val body = resp!!.body?.string().orEmpty()
                var timeStr =
                    org.json.JSONObject(body).optString("value").replace("\"", "").trim()
                if (timeStr.isEmpty()) {
                    XLog.e("value is empty in response: $body")
                }

                // For Test
                val randomDelayMillis = kotlin.random.Random.nextLong(10_000L, 120_000L)
                timeStr = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                    .format(java.util.Date(System.currentTimeMillis() + randomDelayMillis))

                val today =
                    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(java.util.Date())
                val sdf =
                    java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        java.util.Locale.getDefault()
                    )

                var restartDate = sdf.parse("$today $timeStr")

                if (restartDate == null) {
                    XLog.w("parse failed: $today $timeStr")
                } else {
                    if (restartDate.time <= System.currentTimeMillis()) {
                        val cal = java.util.Calendar.getInstance()
                        cal.time = restartDate
                        cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
                        restartDate = cal.time
                    }
                    AlarmService.setAlarm(requireContext(), restartDate.time)
                }
            } catch (e: Exception) {
                XLog.e("Error scheduling restart: ${e.message}", e)
            }
        }
    }

    private fun loadSettings() {
        // 设置到输入框
        binding.etSoftwareVersion.setText(Constants.Version)
        binding.etServerHost.setText(Constants.Host)
        binding.etBrowserHomepage.setText(Constants.HomePage)

        viewModel.stopCheckOnline()
        viewModel.startCheckOnline(arrayOf(Constants.Host, Constants.HomePage))
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
        Constants.Version = softwareVersion
        Constants.Host = serverHost
        Constants.HomePage = browserHomepage

        Toast.makeText(requireContext(), "设置已保存", Toast.LENGTH_SHORT).show()

        loadSettings()
    }

    private fun resetSettings() {
        // 重置为默认值
//        binding.etSoftwareVersion.setText(DEFAULT_SOFTWARE_VERSION)
        binding.etServerHost.setText(Constants.DEFAULT_SERVER_HOST)
        binding.etBrowserHomepage.setText(Constants.DEFAULT_BROWSER_HOMEPAGE)

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
            XLog.e("Failed to unbind service: ${e.message}")
        }
    }
}