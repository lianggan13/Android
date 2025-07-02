package com.yunda.safe.plct.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yunda.safe.plct.common.APK_VERSION
import com.yunda.safe.plct.common.DEFAULT_SERVER_HOST
import com.yunda.safe.plct.common.DEFAULT_SOFTWARE_VERSION
import com.yunda.safe.plct.common.SERVER_HOST
import com.yunda.safe.plct.databinding.FragmentSettingBinding
import com.yunda.safe.plct.utility.Preferences

class SettingFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModels()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
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

        // 设置到输入框
        binding.etSoftwareVersion.setText(softwareVersion)
        binding.etServerHost.setText(serverHost)
    }

    private fun saveSettings() {
        val softwareVersion = binding.etSoftwareVersion.text.toString().trim()
        val serverHost = binding.etServerHost.text.toString().trim()

        // 验证输入
        if (softwareVersion.isEmpty()) {
            Toast.makeText(requireContext(), "请输入软件版本号", Toast.LENGTH_SHORT).show()
            return
        }

        if (serverHost.isEmpty()) {
            Toast.makeText(requireContext(), "请输入服务器地址", Toast.LENGTH_SHORT).show()
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

        // 保存到 SharedPreferences
        Preferences.saveString(APK_VERSION, softwareVersion)
        Preferences.saveString(SERVER_HOST, serverHost)

        Toast.makeText(requireContext(), "设置已保存", Toast.LENGTH_SHORT).show()
    }

    private fun resetSettings() {
        // 重置为默认值
//        binding.etSoftwareVersion.setText(DEFAULT_SOFTWARE_VERSION)
        binding.etServerHost.setText(DEFAULT_SERVER_HOST)

        Toast.makeText(requireContext(), "已重置为默认值", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}