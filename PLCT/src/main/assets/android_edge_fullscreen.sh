#!/system/bin/sh
echo "================================================"
echo "Android Edge浏览器自动全屏脚本"
echo "================================================"

# 配置参数
APP_URL="http://10.60.0.66:5174/#/dashboard"
EDGE_PACKAGE="com.microsoft.emmx"
EDGE_ACTIVITY="com.microsoft.ruby.Main"

# 等待页面加载
echo "[1/3] 等待页面加载完成..."
sleep 2

# 获取屏幕分辨率并模拟点击
echo "[2/3] 模拟用户交互触发全屏..."
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

# 模拟按键序列

echo "→ 专用 REFRESH 键刷新..."
input keyevent 285
sleep 2

# 模拟点击序列

echo "→ 尝试多种全屏方式..."

# 方式1：F11 全屏键（最常用）
echo "→ 尝试F11全屏键..."
input keyevent 142
sleep 2

# 方式2：双击页面中心
echo "→ 双击页面中心..."
input tap "$CENTER_X" "$CENTER_Y"
sleep 0.2
input tap "$CENTER_X" "$CENTER_Y"
sleep 2

# 方式3：点击全屏按钮
echo "→ 点击全屏按钮..."
input tap 1873 156
sleep 2

# 方式4：长按页面（可能弹出全屏选项）
echo "→ 长按页面..."
input swipe "$CENTER_X" "$CENTER_Y" "$CENTER_X" "$CENTER_Y" 1000
sleep 2

# 方式5：空格键（某些情况下有效）
echo "→ 尝试空格键..."
input keyevent 62
sleep 1

echo "[3/3] 脚本执行完成！"
echo "================================================"

