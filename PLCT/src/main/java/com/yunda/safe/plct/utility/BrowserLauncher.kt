package com.yunda.safe.plct.utility


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.elvishew.xlog.XLog
import com.yunda.safe.plct.common.BROWSER_HOMEPAGE
import com.yunda.safe.plct.common.DEFAULT_BROWSER_HOMEPAGE
import java.net.HttpURLConnection
import java.net.URL

object BrowserLauncher {

    // 坐标百分比常量配置（基于 1920x1080 分辨率）
    private const val REFRESH_BUTTON_X_PERCENT = 1800.0 / 1920.0  // 93.75%
    private const val REFRESH_BUTTON_Y_PERCENT = 88.0 / 1080.0    // 8.15%
    private const val FULLSCREEN_BUTTON_X_PERCENT = 1873.0 / 1920.0  // 97.55%
    private const val FULLSCREEN_BUTTON_Y_PERCENT = 156.0 / 1080.0   // 14.44%

    // 按钮尺寸
    private const val REFRESH_BUTTON_WIDTH = 25
    private const val REFRESH_BUTTON_HEIGHT = 25
    private const val FULLSCREEN_BUTTON_WIDTH = 60
    private const val FULLSCREEN_BUTTON_HEIGHT = 32

    /**
     * 根据屏幕分辨率和百分比计算实际坐标
     */
    private fun calculateCoordinatesByPercent(
        screenWidth: Int,
        screenHeight: Int,
        xPercent: Double,
        yPercent: Double
    ): Pair<Int, Int> {
        val x = (screenWidth * xPercent).toInt()
        val y = (screenHeight * yPercent).toInt()
        return Pair(x, y)
    }

    /**
     * 计算按钮中心和四角坐标
     */
    private fun calcButtonCornerPoints(
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
     * 处理脚本内容，动态插入计算出的坐标点
     */
    private fun processScriptWithDynamicCoordinates(originalScript: String): String {
        // 获取当前屏幕分辨率
        val screenSize = getScreenSize()
        val screenWidth = screenSize.first
        val screenHeight = screenSize.second

        // 根据屏幕分辨率和百分比计算实际坐标
        val refreshButtonCoord = calculateCoordinatesByPercent(
            screenWidth, screenHeight,
            REFRESH_BUTTON_X_PERCENT, REFRESH_BUTTON_Y_PERCENT
        )
        val fullscreenButtonCoord = calculateCoordinatesByPercent(
            screenWidth, screenHeight,
            FULLSCREEN_BUTTON_X_PERCENT, FULLSCREEN_BUTTON_Y_PERCENT
        )

        XLog.i("Screen resolution: ${screenWidth}x${screenHeight}")
        XLog.i("Refresh button percentage: ${REFRESH_BUTTON_X_PERCENT}, ${REFRESH_BUTTON_Y_PERCENT}")
        XLog.i("Fullscreen button percentage: ${FULLSCREEN_BUTTON_X_PERCENT}, ${FULLSCREEN_BUTTON_Y_PERCENT}")
        XLog.i("Calculated refresh button coordinates: (${refreshButtonCoord.first}, ${refreshButtonCoord.second})")
        XLog.i("Calculated fullscreen button coordinates: (${fullscreenButtonCoord.first}, ${fullscreenButtonCoord.second})")

        // 动态计算刷新按钮坐标点
        val refreshButtonPoints = calcButtonCornerPoints(
            refreshButtonCoord.first, refreshButtonCoord.second,
            REFRESH_BUTTON_WIDTH, REFRESH_BUTTON_HEIGHT
        )
        val refreshCenterPoint = refreshButtonPoints[0]
        val refreshLeftTopPoint = refreshButtonPoints[1]
        val refreshRightTopPoint = refreshButtonPoints[2]
        val refreshLeftBottomPoint = refreshButtonPoints[3]
        val refreshRightBottomPoint = refreshButtonPoints[4]

        // 动态计算全屏按钮坐标点
        val fullscreenButtonPoints = calcButtonCornerPoints(
            fullscreenButtonCoord.first, fullscreenButtonCoord.second,
            FULLSCREEN_BUTTON_WIDTH, FULLSCREEN_BUTTON_HEIGHT
        )
        val fullscreenCenterPoint = fullscreenButtonPoints[0]
        val fullscreenLeftTopPoint = fullscreenButtonPoints[1]
        val fullscreenRightTopPoint = fullscreenButtonPoints[2]
        val fullscreenLeftBottomPoint = fullscreenButtonPoints[3]
        val fullscreenRightBottomPoint = fullscreenButtonPoints[4]

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
     * 关闭Edge浏览器
     */
    private fun closeEdgeBrowser(sync: Boolean = false) {
        val closeAction = {
            try {
                XLog.i("Closing Edge browser...")

                // 方法1：使用 am 命令强制停止 Edge 浏览器
                val stopProcess = Runtime.getRuntime()
                    .exec(arrayOf("su", "-c", "am force-stop com.microsoft.emmx"))
                stopProcess.waitFor()

                XLog.i("Edge browser close commands executed")

                // 等待一段时间确保浏览器完全关闭
                Thread.sleep(2000)

            } catch (e: Exception) {
                XLog.w("Failed to close Edge browser: ${e.message}")
            }
        }

        if (sync) {
            // 同步执行
            closeAction()
        } else {
            // 异步执行
            Thread { closeAction() }.start()
        }
    }

    /**
     * 检查网页HTTP状态
     */
    private fun getHttpStatus(url: String): Int {
        return try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "HEAD"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)")
            conn.connect()

            val code = conn.responseCode
            XLog.i("HTTP Status for $url: $code")

            conn.disconnect()
            code

        } catch (e: java.net.SocketTimeoutException) {
            XLog.w("Connection timeout for $url: ${e.message}")
            -1
        } catch (e: java.net.ConnectException) {
            XLog.w("Connection failed for $url: ${e.message}")
            -2
        } catch (e: Exception) {
            XLog.w("HTTP check failed for $url: ${e.message}")
            -3
        }
    }

    /**
     * 等待网站可访问并启动浏览器
     * @param context 上下文
     * @param url 要访问的网站URL
     * @param showToast 是否显示Toast提示（后台服务建议设为false）
     */
    fun waitForWebsiteAndLaunch(context: Context, url: String, showToast: Boolean = true) {
        Thread {
            try {
                XLog.i("Starting waitForWebsiteAndLaunch for URL: $url")

                // 首先同步关闭已存在的Edge浏览器
                closeEdgeBrowser(sync = true)

                var attempts = 0
                val maxAttempts = 12
                val checkInterval = 5000L

                while (attempts < maxAttempts) {
                    try {
                        attempts++
                        XLog.i("Checking website accessibility... Attempt $attempts/$maxAttempts")

                        val httpStatus = getHttpStatus(url)
                        XLog.i("HTTP Status Code: $httpStatus (Attempt $attempts)")

                        if (httpStatus == 200) {
                            XLog.i("Website is now accessible! Launching Edge browser...")

                            // 在主线程中启动浏览器
                            Handler(Looper.getMainLooper()).post {
                                launchBrowserWithValidUrl(context, url, showToast)
                            }
                            return@Thread

                        } else {
                            XLog.w("Website still not accessible (Status: $httpStatus), waiting ${checkInterval / 1000}s...")

                            // 在主线程显示等待状态
                            if (showToast) {
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(
                                        context,
                                        "等待网站启动... ($attempts/$maxAttempts)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            Thread.sleep(checkInterval)
                        }

                    } catch (e: Exception) {
                        XLog.e("Error in attempt $attempts: ${e.message}", e)
                        Thread.sleep(checkInterval)
                    }
                }

                // 超过最大尝试次数后的处理
                XLog.w("Maximum attempts reached, launching browser anyway...")
                if (showToast) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            context,
                            "网站可能尚未完全启动，建议手动点击页面或检查浏览器设置",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                XLog.e("Fatal error in waitForWebsiteAndLaunch: ${e.message}", e)
            }
        }.start()
    }

    /**
     * 启动浏览器（在确认URL可访问后）
     */
    private fun launchBrowserWithValidUrl(
        context: Context,
        url: String,
        showToast: Boolean = true
    ) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.setPackage("com.microsoft.emmx") // Edge 浏览器包名
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            context.startActivity(intent)

            XLog.i("Successfully launched Edge browser with URL: $url")

            if (showToast) {
                Toast.makeText(context, "Edge 浏览器已启动", Toast.LENGTH_SHORT).show()
            }

            // 延迟执行脚本，等待 Edge 启动完成
            Handler(Looper.getMainLooper()).postDelayed({
                executeFullscreenScript(context)
            }, 3000)

        } catch (e: Exception) {
            XLog.e("Failed to launch Edge browser: ${e.message}", e)

            // 尝试启动默认浏览器
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(fallbackIntent)

                if (showToast) {
                    Toast.makeText(context, "已启动默认浏览器", Toast.LENGTH_SHORT).show()
                }
                XLog.i("Fallback: launched default browser")
            } catch (fallbackException: Exception) {
                XLog.e(
                    "Failed to launch any browser: ${fallbackException.message}",
                    fallbackException
                )
                if (showToast) {
                    Toast.makeText(context, "无法启动浏览器", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 执行全屏脚本
     */
    private fun executeFullscreenScript(context: Context) {
        Thread {
            try {
                XLog.i("Starting to execute fullscreen script")

                // 从 assets 读取脚本内容并处理动态坐标
                val originalScript =
                    context.assets.open("android_edge_fullscreen.sh").bufferedReader()
                        .use { it.readText() }
                val scriptContent = processScriptWithDynamicCoordinates(originalScript)
                val scriptPath = "/sdcard/android_edge_fullscreen.sh"

                // 使用 cat 命令写入脚本文件
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
     * 启动浏览器（使用配置的主页地址）
     */
    fun launchBrowserWithHomepage(context: Context, showToast: Boolean = true) {
        return
        val browserHomepage = Preferences.getString(BROWSER_HOMEPAGE, DEFAULT_BROWSER_HOMEPAGE)

        if (browserHomepage != null && browserHomepage.isNotEmpty() &&
            (browserHomepage.startsWith("http://") || browserHomepage.startsWith("https://"))
        ) {

            XLog.i("Launching browser with homepage: $browserHomepage")
            waitForWebsiteAndLaunch(context, browserHomepage, showToast)

        } else {
            XLog.w("Invalid browser homepage format: $browserHomepage")
            if (showToast) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "浏览器主页地址格式不正确", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}