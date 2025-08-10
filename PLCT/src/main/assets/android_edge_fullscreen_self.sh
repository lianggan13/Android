#!/system/bin/sh
# Android Shell脚本 - 可在多种环境下运行
# 文件名: android_edge_fullscreen.sh
# 
# 执行方法：
# 方法1: adb push android_edge_fullscreen.sh /sdcard/ && adb shell "sh /sdcard/android_edge_fullscreen.sh"
# 方法2: adb shell < android_edge_fullscreen.sh
# 方法3: 在Termux中: chmod +x android_edge_fullscreen.sh && ./android_edge_fullscreen.sh
# 方法4: Root设备: su -c "sh /sdcard/android_edge_fullscreen.sh"

echo "================================================"
echo "Android Edge浏览器自动全屏脚本"
echo "================================================"

# 配置参数
APP_URL="http://10.60.0.66:5174/"
EDGE_PACKAGE="com.microsoft.emmx"
EDGE_ACTIVITY="com.microsoft.ruby.Main"

# 检查Edge是否已安装
echo "[1/5] 检查Microsoft Edge是否已安装..."
if pm list packages | grep -q "$EDGE_PACKAGE"; then
    echo "✓ Microsoft Edge已安装"
    USE_EDGE=true
else
    echo "✗ Microsoft Edge未安装，将使用默认浏览器"
    USE_EDGE=false
fi

# 关闭可能已运行的浏览器实例
echo "[2/5] 清理旧的浏览器实例..."
am force-stop "$EDGE_PACKAGE"
sleep 1

# 启动浏览器
echo "[3/5] 启动浏览器..."
if [ "$USE_EDGE" = true ]; then
    echo "使用Edge浏览器打开: $APP_URL"
    am start -n "$EDGE_PACKAGE/$EDGE_ACTIVITY" -d "$APP_URL"
    
    # 如果失败，使用Intent方式
    if [ $? -ne 0 ]; then
        echo "尝试备用启动方式..."
        am start -a android.intent.action.VIEW -d "$APP_URL" "$EDGE_PACKAGE"
    fi
    
    # 如果还失败，使用默认浏览器
    if [ $? -ne 0 ]; then
        echo "Edge启动失败，使用默认浏览器..."
        am start -a android.intent.action.VIEW -d "$APP_URL"
    fi
else
    echo "使用默认浏览器打开: $APP_URL"
    am start -a android.intent.action.VIEW -d "$APP_URL"
fi

# 等待页面加载
echo "[4/5] 等待页面加载完成..."
sleep 6

# 获取屏幕分辨率并模拟点击
echo "[5/5] 模拟用户交互触发全屏..."
SCREEN_SIZE=$(wm size | grep "Physical size" | cut -d: -f2 | tr -d ' ')
if [ -n "$SCREEN_SIZE" ]; then
    WIDTH=$(echo "$SCREEN_SIZE" | cut -dx -f1)
    HEIGHT=$(echo "$SCREEN_SIZE" | cut -dx -f2)
    CENTER_X=$((WIDTH / 2))
    CENTER_Y=$((HEIGHT / 2))
    TOP_Y=$((HEIGHT / 4))
    
    echo "✓ 屏幕分辨率: ${WIDTH}x${HEIGHT}"
    echo "✓ 中心点: ${CENTER_X}, ${CENTER_Y}"
else
    # 默认分辨率
    CENTER_X=960
    CENTER_Y=540
    TOP_Y=270
    echo "⚠ 使用默认分辨率: 1920x1080"
fi

# 模拟点击序列
echo "→ 点击页面中心..."
input tap "$CENTER_X" "$CENTER_Y"
sleep 1

echo "→ 点击页面上部..."
input tap "$CENTER_X" "$TOP_Y"
sleep 1

echo "→ 尝试空格键..."
input keyevent KEYCODE_SPACE
sleep 1

echo "→ 尝试F11全屏键..."
input keyevent KEYCODE_F11
sleep 2

# 检查结果
echo "================================================"
if dumpsys window | grep -q "mIsFullscreen=true"; then
    echo "✓ 成功！已进入全屏模式"
else
    echo "⚠ 警告：可能未完全进入全屏模式"
    echo "建议手动点击页面或检查浏览器设置"
fi

echo "================================================"
echo "脚本执行完成！"
echo "================================================"
