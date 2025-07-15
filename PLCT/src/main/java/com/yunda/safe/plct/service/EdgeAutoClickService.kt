package com.yunda.safe.plct.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.elvishew.xlog.XLog

class EdgeAutoClickService : AccessibilityService() {

    private var hasClicked = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        XLog.i("EdgeAutoClickService: Event received - Package: ${event.packageName}, EventType: ${event.eventType}")

        // 只处理 Edge 浏览器窗口
        if (event.packageName == "com.microsoft.emmx") {
            XLog.i("EdgeAutoClickService: Edge browser event detected, hasClicked: $hasClicked")

            if (!hasClicked) {
                hasClicked = true
                XLog.i("EdgeAutoClickService: Starting delayed click sequence")

                // 延迟 3 秒，等待页面加载
                Handler(Looper.getMainLooper()).postDelayed({
                    XLog.i("EdgeAutoClickService: Searching for web controls")

                    // 方案1：尝试查找特定文本的按钮
                    findAndClickButtonByText("全屏", "fullscreen", "Fullscreen", "FULLSCREEN")

                    // 方案2：如果没找到按钮，尝试查找其他可点击元素
                    Handler(Looper.getMainLooper()).postDelayed({
                        findAndClickByDescription("全屏按钮", "fullscreen button")
                    }, 1000)

                    // 方案3：如果都没找到，执行默认点击
                    Handler(Looper.getMainLooper()).postDelayed({
                        performCenterClick()
                    }, 2000)

                }, 3000)
            }
        }
    }

    override fun onInterrupt() {
        XLog.i("EdgeAutoClickService: Service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        XLog.i("EdgeAutoClickService: Service connected and ready")
        hasClicked = false // 重置状态
    }

    /**
     * 根据文本内容查找并点击全屏
     */
    private fun findAndClickButtonByText(vararg texts: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false

        for (text in texts) {
            val nodes = rootNode.findAccessibilityNodeInfosByText(text)
            for (node in nodes) {
                if (node.isClickable) {
                    XLog.i("EdgeAutoClickService: Found clickable button with text: $text")
                    val success = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    if (success) {
                        XLog.i("EdgeAutoClickService: Successfully clicked button: $text")
                        return true
                    }
                }
            }
        }

        XLog.w("EdgeAutoClickService: No clickable button found with given texts")
        return false
    }

    /**
     * 根据内容描述查找并点击控件
     */
    private fun findAndClickByDescription(vararg descriptions: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false

        for (description in descriptions) {
            val found = findNodeByDescription(rootNode, description)
            if (found != null && found.isClickable) {
                XLog.i("EdgeAutoClickService: Found clickable element with description: $description")
                val success = found.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                if (success) {
                    XLog.i("EdgeAutoClickService: Successfully clicked element: $description")
                    return true
                }
            }
        }

        XLog.w("EdgeAutoClickService: No clickable element found with given descriptions")
        return false
    }

    /**
     * 递归查找具有特定描述的节点
     */
    private fun findNodeByDescription(
        node: AccessibilityNodeInfo,
        description: String
    ): AccessibilityNodeInfo? {
        if (node.contentDescription?.contains(description, ignoreCase = true) == true) {
            return node
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findNodeByDescription(child, description)
            if (found != null) return found
        }

        return null
    }

    /**
     * 查找特定类型的控件（如 Button、ImageButton 等）
     */
    private fun findAndClickByClassName(vararg classNames: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false

        for (className in classNames) {
            val found = findNodeByClassName(rootNode, className)
            if (found != null && found.isClickable) {
                XLog.i("EdgeAutoClickService: Found clickable element with class: $className")
                val success = found.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                if (success) {
                    XLog.i("EdgeAutoClickService: Successfully clicked element: $className")
                    return true
                }
            }
        }

        return false
    }

    /**
     * 递归查找具有特定类名的节点
     */
    private fun findNodeByClassName(
        node: AccessibilityNodeInfo,
        className: String
    ): AccessibilityNodeInfo? {
        if (node.className?.contains(className, ignoreCase = true) == true) {
            return node
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findNodeByClassName(child, className)
            if (found != null) return found
        }

        return null
    }

    /**
     * 获取控件的屏幕坐标并执行手势点击
     */
    private fun clickNodeByCoordinates(node: AccessibilityNodeInfo): Boolean {
        val rect = Rect()
        node.getBoundsInScreen(rect)

        val centerX = (rect.left + rect.right) / 2
        val centerY = (rect.top + rect.bottom) / 2

        XLog.i("EdgeAutoClickService: Clicking coordinates: ($centerX, $centerY)")

        val path = Path()
        path.moveTo(centerX.toFloat(), centerY.toFloat())
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        return dispatchGesture(gesture, null, null)
    }

    /**
     * 遍历并打印所有可点击的控件（调试用）
     */
    private fun logAllClickableElements() {
        val rootNode = rootInActiveWindow ?: return
        XLog.i("EdgeAutoClickService: === 开始遍历所有可点击元素 ===")
        logClickableNode(rootNode, 0)
        XLog.i("EdgeAutoClickService: === 遍历结束 ===")
    }

    private fun logClickableNode(node: AccessibilityNodeInfo, depth: Int) {
        val indent = "  ".repeat(depth)
        if (node.isClickable) {
            XLog.i("EdgeAutoClickService: ${indent}可点击: ${node.className} - '${node.text}' - '${node.contentDescription}'")
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            logClickableNode(child, depth + 1)
        }
    }

    private fun performCenterClick() {
        // 获取屏幕分辨率
        val wm = getSystemService(WINDOW_SERVICE) as android.view.WindowManager
        val display = wm.defaultDisplay
        val size = android.graphics.Point()
        display.getRealSize(size)
        val centerX = size.x / 2
        val centerY = size.y / 2

        // 构造点击手势
        val path = Path()
        path.moveTo(centerX.toFloat(), centerY.toFloat())
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        val result = dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                XLog.i("EdgeAutoClickService: Center click performed")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                XLog.w("EdgeAutoClickService: Center click cancelled")
            }
        }, null)
        if (!result) {
            XLog.w("EdgeAutoClickService: dispatchGesture failed")
        }
    }

    // 如需点击顶部区域
    private fun performTopClick() {
        val wm = getSystemService(WINDOW_SERVICE) as android.view.WindowManager
        val display = wm.defaultDisplay
        val size = android.graphics.Point()
        display.getRealSize(size)
        val centerX = size.x / 2
        val topY = size.y / 4

        val path = Path()
        path.moveTo(centerX.toFloat(), topY.toFloat())
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()

        dispatchGesture(gesture, null, null)
    }
}