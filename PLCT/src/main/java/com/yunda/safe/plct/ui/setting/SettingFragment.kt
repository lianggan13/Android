package com.yunda.safe.plct.ui.setting

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.elvishew.xlog.XLog
import com.yunda.safe.plct.common.APK_VERSION
import com.yunda.safe.plct.common.BROWSER_HOMEPAGE
import com.yunda.safe.plct.common.DEFAULT_BROWSER_HOMEPAGE
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
            launchEdgeBrowser(browserHomepage)
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

        // 使用用户配置的浏览器主页启动 Edge 浏览器
        launchEdgeBrowser(browserHomepage)

        Toast.makeText(requireContext(), "设置已保存", Toast.LENGTH_SHORT).show()
    }

    private fun resetSettings() {
        // 重置为默认值
//        binding.etSoftwareVersion.setText(DEFAULT_SOFTWARE_VERSION)
        binding.etServerHost.setText(DEFAULT_SERVER_HOST)
        binding.etBrowserHomepage.setText(DEFAULT_BROWSER_HOMEPAGE)

        Toast.makeText(requireContext(), "已重置为默认值", Toast.LENGTH_SHORT).show()
    }


    private fun launchEdgeBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.setPackage("com.microsoft.emmx") // Edge 浏览器包名
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // 创建启动选项
            val options = ActivityOptions.makeBasic()

            // 选项1：无动画启动（更快）
            options.setLaunchDisplayId(0) // 主显示器

            startActivity(intent, options.toBundle())

            XLog.i("Successfully launched Edge browser with URL: $url")

            // 延迟执行脚本，等待 Edge 启动完成
            Handler(Looper.getMainLooper()).postDelayed({
                executeFullscreenScript()
            }, 3000)

        } catch (e: Exception) {
            XLog.e("Failed to launch any browser: ${e.message}", e)
        }
    }

    private fun executeFullscreenScript() {
        Thread {
            try {
                XLog.i("Starting to execute fullscreen script")

                // 从 assets 读取脚本内容
                val scriptContent = readScriptFromAssets()
                val scriptPath = "/sdcard/android_edge_fullscreen.sh"

                // 使用 cat 命令写入脚本文件，避免单引号问题
                val writeProcess = Runtime.getRuntime().exec("su")
                val writer = writeProcess.outputStream.bufferedWriter()
                writer.write("cat > $scriptPath << 'EOF'\n")
                writer.write(scriptContent)
                writer.write("\nEOF\n")
                writer.write("chmod +x $scriptPath\n")
                writer.write("exit\n")
                writer.flush()
                writer.close()
                writeProcess.waitFor()

                // 执行脚本
                val execProcess = Runtime.getRuntime().exec(arrayOf("su", "-c", "sh $scriptPath"))

                // 读取输出
                val output = execProcess.inputStream.bufferedReader().readText()
                val error = execProcess.errorStream.bufferedReader().readText()

                val exitCode = execProcess.waitFor()

                XLog.i("Script execution completed with exit code: $exitCode")
                XLog.i("Script output: $output")
                if (error.isNotEmpty()) {
                    XLog.w("Script error: $error")
                }

            } catch (e: Exception) {
                XLog.e("Failed to execute script: ${e.message}", e)
            }
        }.start()
    }

    /**
     * 获取屏幕分辨率
     */
    private fun getScreenSize(): Pair<Int, Int> {
        return try {
            val process = Runtime.getRuntime().exec("wm size")
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()

            // 解析输出，例如："Physical size: 1920x1080"
            val sizeRegex = """(\d+)x(\d+)""".toRegex()
            val matchResult = sizeRegex.find(output)

            if (matchResult != null) {
                val width = matchResult.groupValues[1].toInt()
                val height = matchResult.groupValues[2].toInt()
                XLog.i("Screen size detected: ${width}x${height}")
                Pair(width, height)
            } else {
                XLog.w("Could not parse screen size, using default")
                Pair(1920, 1080) // 默认分辨率
            }
        } catch (e: Exception) {
            XLog.w("Failed to get screen size: ${e.message}")
            Pair(1920, 1080) // 默认分辨率
        }
    }

    /**
     * 从 assets 目录读取脚本文件内容并动态插入坐标点
     */
    private fun readScriptFromAssets(): String {
        val originalScript =
            requireActivity().assets.open("android_edge_fullscreen.sh").bufferedReader()
                .use { reader ->
                    reader.readText()
                }
        // 动态处理脚本内容，插入计算出的坐标点
        val script = processScriptWithDynamicCoordinates(originalScript)
        return script
    }

    /**
     * 处理脚本内容，动态插入计算出的坐标点
     */
    private fun processScriptWithDynamicCoordinates(originalScript: String): String {
        // 动态计算刷新按钮坐标点
        val refreshButtonPoints = calcButtonCornerPoints(1800, 88, 25, 25)
        val refreshCenterPoint = refreshButtonPoints[0]
        val refreshLeftTopPoint = refreshButtonPoints[1]
        val refreshRightTopPoint = refreshButtonPoints[2]
        val refreshLeftBottomPoint = refreshButtonPoints[3]
        val refreshRightBottomPoint = refreshButtonPoints[4]

        // 动态计算全屏按钮坐标点
        val fullscreenButtonPoints = calcButtonCornerPoints(1873, 156, 60, 32)
        val fullscreenCenterPoint = fullscreenButtonPoints[0]
        val fullscreenLeftTopPoint = fullscreenButtonPoints[1]
        val fullscreenRightTopPoint = fullscreenButtonPoints[2]
        val fullscreenLeftBottomPoint = fullscreenButtonPoints[3]
        val fullscreenRightBottomPoint = fullscreenButtonPoints[4]

        // 记录计算出的坐标点
        // XLog.i("Dynamic coordinates calculated:")
        // XLog.i("  Refresh Button:")
        // XLog.i("    Center: (${refreshCenterPoint.first}, ${refreshCenterPoint.second})")
        // XLog.i("    LeftTop: (${refreshLeftTopPoint.first}, ${refreshLeftTopPoint.second})")
        // XLog.i("    RightTop: (${refreshRightTopPoint.first}, ${refreshRightTopPoint.second})")
        // XLog.i("    LeftBottom: (${refreshLeftBottomPoint.first}, ${refreshLeftBottomPoint.second})")
        // XLog.i("    RightBottom: (${refreshRightBottomPoint.first}, ${refreshRightBottomPoint.second})")

        XLog.i("  Fullscreen Button:")
        XLog.i("    Center: (${fullscreenCenterPoint.first}, ${fullscreenCenterPoint.second})")
        XLog.i("    LeftTop: (${fullscreenLeftTopPoint.first}, ${fullscreenLeftTopPoint.second})")
        XLog.i("    RightTop: (${fullscreenRightTopPoint.first}, ${fullscreenRightTopPoint.second})")
        XLog.i("    LeftBottom: (${fullscreenLeftBottomPoint.first}, ${fullscreenLeftBottomPoint.second})")
        XLog.i("    RightBottom: (${fullscreenRightBottomPoint.first}, ${fullscreenRightBottomPoint.second})")

        // 构建动态的点击序列 - 先点击刷新按钮，再点击全屏按钮
        val dynamicClickSequence = """
        # echo "→ 点击刷新按钮中心 (${refreshCenterPoint.first}, ${refreshCenterPoint.second})..."
        # input tap ${refreshCenterPoint.first} ${refreshCenterPoint.second}
        # sleep 1

        # echo "→ 点击刷新按钮左上角 (${refreshLeftTopPoint.first}, ${refreshLeftTopPoint.second})..."
        # input tap ${refreshLeftTopPoint.first} ${refreshLeftTopPoint.second}
        # sleep 1

        # echo "→ 点击刷新按钮右上角 (${refreshRightTopPoint.first}, ${refreshRightTopPoint.second})..."
        # input tap ${refreshRightTopPoint.first} ${refreshRightTopPoint.second}
        # sleep 1
 
        # echo "→ 点击刷新按钮左下角 (${refreshLeftBottomPoint.first}, ${refreshLeftBottomPoint.second})..."
        # input tap ${refreshLeftBottomPoint.first} ${refreshLeftBottomPoint.second}
        # sleep 1

        # echo "→ 点击刷新按钮右下角 (${refreshRightBottomPoint.first}, ${refreshRightBottomPoint.second})..."
        # input tap ${refreshRightBottomPoint.first} ${refreshRightBottomPoint.second}
        # sleep 2
 
        # echo "→ 等待页面刷新完成..."
        # sleep 3

        echo "→ 点击全屏按钮中心 (${fullscreenCenterPoint.first}, ${fullscreenCenterPoint.second})..."
        input tap ${fullscreenCenterPoint.first} ${fullscreenCenterPoint.second}
        sleep 1

        # echo "→ 点击全屏按钮左上角 (${fullscreenLeftTopPoint.first}, ${fullscreenLeftTopPoint.second})..."
        # input tap ${fullscreenLeftTopPoint.first} ${fullscreenLeftTopPoint.second}
        # sleep 1

        # echo "→ 点击全屏按钮右上角 (${fullscreenRightTopPoint.first}, ${fullscreenRightTopPoint.second})..."
        # input tap ${fullscreenRightTopPoint.first} ${fullscreenRightTopPoint.second}
        # sleep 1
 
        # echo "→ 点击全屏按钮左下角 (${fullscreenLeftBottomPoint.first}, ${fullscreenLeftBottomPoint.second})..."
        # input tap ${fullscreenLeftBottomPoint.first} ${fullscreenLeftBottomPoint.second}
        # sleep 1
 
        # echo "→ 点击全屏按钮右下角 (${fullscreenRightBottomPoint.first}, ${fullscreenRightBottomPoint.second})..."
        # input tap ${fullscreenRightBottomPoint.first} ${fullscreenRightBottomPoint.second}
        # sleep 1
        """.trimIndent()

        // 查找"模拟点击序列"标记并在其后插入动态坐标
        val clickSequenceMarker = "# 模拟点击序列"
        val insertPoint = originalScript.indexOf(clickSequenceMarker)

        return if (insertPoint != -1) {
            XLog.i("Found click sequence marker, inserting dynamic coordinates after it")
            // 找到标记后的换行位置
            val afterMarker = originalScript.indexOf('\n', insertPoint)
            if (afterMarker != -1) {
                // 在标记后插入动态点击序列
                originalScript.substring(0, afterMarker + 1) +
                        dynamicClickSequence + "\n\n" +
                        originalScript.substring(afterMarker + 1)
            } else {
                // 如果没有找到换行，直接在标记后添加
                originalScript.substring(0, insertPoint + clickSequenceMarker.length) +
                        "\n" + dynamicClickSequence + "\n\n" +
                        originalScript.substring(insertPoint + clickSequenceMarker.length)
            }
        } else {
            // 如果没有找到模拟点击序列标记，尝试在空格键前插入
            val keyboardInsertPoint = originalScript.indexOf("echo \"→ 尝试空格键...\"")
            if (keyboardInsertPoint != -1) {
                XLog.i("Click sequence marker not found, inserting before keyboard commands")
                originalScript.substring(0, keyboardInsertPoint) +
                        dynamicClickSequence + "\n\n" +
                        originalScript.substring(keyboardInsertPoint)
            } else {
                // 如果都找不到，直接返回原脚本
                XLog.w("Could not find insertion point, using original script")
                originalScript
            }
        }
    }

    /**
     * 计算按钮中心和四角坐标
     * @param centerX 按钮中心X坐标
     * @param centerY 按钮中心Y坐标
     * @param width 按钮宽度
     * @param height 按钮高度
     * @param offsetRatio 偏移比例，默认0.25（即1/4）
     * @return List<Pair<Int, Int>>，顺序为 center, leftTop, rightTop, leftBottom, rightBottom
     */
    fun calcButtonCornerPoints(
        centerX: Int,
        centerY: Int,
        width: Int,
        height: Int,
        offsetRatio: Float = 0.25f
    ): List<Pair<Int, Int>> {
        val dx = (width * offsetRatio).toInt()
        val dy = (height * offsetRatio).toInt()
        return listOf(
            Pair(centerX, centerY), // center
            Pair(centerX - dx, centerY - dy), // leftTop
            Pair(centerX + dx, centerY - dy), // rightTop
            Pair(centerX - dx, centerY + dy), // leftBottom
            Pair(centerX + dx, centerY + dy)  // rightBottom
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}