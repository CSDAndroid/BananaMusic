# 导航栏高斯模糊效果实现

## 功能概述

本项目为Android音乐应用的导航栏实现了高斯模糊效果，使用Android 12 (API 31) 及以上版本的RenderEffect API。

## 实现原理

### 1. 技术栈
- **RenderEffect API**: Android 12+ 提供的硬件加速模糊效果
- **FrameLayout**: 导航栏容器布局
- **半透明背景**: 配合模糊效果实现毛玻璃质感

### 2. 核心组件

#### BlurEffectManager.kt
- 高斯模糊效果管理器
- 提供模糊效果的应用和移除功能
- 自动检测设备兼容性
- **API兼容性处理**: 支持minSdk 24，自动降级处理

#### Nav.kt (基类)
- 所有使用导航栏的Activity的基类
- 在onCreate时自动应用模糊效果
- 统一管理导航栏状态

#### nav_all.xml
- 导航栏主布局文件
- 包含音乐栏、导航按钮、搜索按钮

## 文件结构

```
app/src/main/
├── java/com/example/musicapp/
│   ├── BlurEffectManager.kt          # 模糊效果管理器
│   ├── Nav.kt                        # 导航栏基类
│   ├── MainActivity.kt               # 主页Activity
│   ├── MineActivity.kt               # 我的页面Activity
│   └── NavMusicActivity.kt           # 音乐页面Activity
├── res/layout/
│   ├── nav_all.xml                   # 导航栏主布局
│   ├── nav.xml                       # 导航按钮布局
│   ├── music_bar.xml                 # 音乐栏布局
│   └── search_button.xml             # 搜索按钮布局
└── res/drawable/
    ├── nav_frosted_glass.xml         # 毛玻璃背景
    └── ios_liquid_glass.xml          # 搜索按钮背景
```

## 使用方法

### 1. 自动应用
所有继承自`Nav`类的Activity会自动应用高斯模糊效果：

```kotlin
class MainActivity : Nav() {
    // 无需额外代码，模糊效果会自动应用
}
```

### 2. 手动控制
如果需要手动控制模糊效果：

```kotlin
// 应用模糊效果
val navContainer = findViewById<View>(R.id.nav_all_container)
BlurEffectManager.applyBlurToNavBar(navContainer)

// 移除模糊效果
BlurEffectManager.removeBlurFromNavBar(navContainer)
```

## 兼容性

- **最低支持**: Android 7.0 (API 24) - 应用minSdk
- **模糊效果支持**: Android 12+ (API 31) - RenderEffect API
- **自动降级**: 在不支持的设备上，导航栏会显示为半透明背景
- **性能优化**: 使用硬件加速，性能影响最小

## API兼容性处理

### 1. 运行时检查
```kotlin
fun applyBlurToNavBar(navContainer: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        applyBlurToNavBarInternal(navContainer)
    } else {
        Log.w(TAG, "当前设备API级别过低，无法应用高斯模糊效果")
    }
}
```

### 2. 优雅降级
- API 31+ 设备：显示完整的高斯模糊效果
- API 24-30 设备：显示半透明背景，保持现代化视觉效果
- 不影响应用的基本功能和性能

## 自定义配置

### 1. 模糊强度
在`BlurEffectManager.kt`中修改模糊半径：

```kotlin
val blurEffect = RenderEffect.createBlurEffect(
    25f, // 水平模糊半径
    25f, // 垂直模糊半径
    Shader.TileMode.CLAMP
)
```

### 2. 背景透明度
在各个布局文件中修改背景颜色：

```xml
app:cardBackgroundColor="#CCFFFFFF" <!-- 80%透明度 -->
```

### 3. 圆角半径
在drawable文件中修改圆角：

```xml
<corners android:radius="20dp" />
```

## 调试

启用日志输出查看模糊效果状态：

```bash
adb logcat | grep -E "(BlurEffectManager|Nav)"
```

## 注意事项

1. **性能考虑**: 模糊效果会消耗一定的GPU资源
2. **内存使用**: RenderEffect会占用额外的显存
3. **兼容性**: 确保目标设备支持Android 12+
4. **测试**: 在不同设备上测试模糊效果的显示效果
5. **API级别**: 应用支持minSdk 24，但模糊效果需要API 31+

## 效果预览

- 导航栏背景呈现毛玻璃质感
- 背景内容透过导航栏产生模糊效果
- 保持导航栏内容的清晰度
- 现代化的视觉体验

## 修复的问题

### 1. API兼容性问题
- ✅ 修复了`renderEffect`属性访问的API级别问题
- ✅ 修复了`applyBlurToNavBar`方法的API级别问题
- ✅ 添加了运行时API级别检查
- ✅ 实现了优雅降级机制

### 2. 编译错误
- ✅ 移除了对API 31+特有属性的直接访问
- ✅ 使用内部方法包装API 31+功能
- ✅ 确保代码在所有支持的API级别上都能编译通过 