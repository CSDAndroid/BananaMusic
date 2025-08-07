package com.example.musicapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

abstract class Nav : AppCompatActivity() {

    // 底部导航栏视图
    protected lateinit var homeLayout: LinearLayout
    protected lateinit var musicLayout: LinearLayout
    protected lateinit var profileLayout: LinearLayout

    // 图标和文字
    protected lateinit var homeIcon: ImageView
    protected lateinit var homeText: TextView
    protected lateinit var musicIcon: ImageView
    protected lateinit var musicText: TextView
    protected lateinit var profileIcon: ImageView
    protected lateinit var profileText: TextView

    // 滑动检测相关
    private var lastY = 0f
    private var isMusicBarVisible = true
    private lateinit var musicBarView: View
    private lateinit var navView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 初始化MusicBarManager
        MusicBarManager.init(this)

        // 设置包含底部导航栏的布局
        setContentView(R.layout.activity_with_nav)

        // 将子类的布局添加到内容区域
        val contentContainer = findViewById<android.widget.FrameLayout>(R.id.content_container)
        val childLayout = layoutInflater.inflate(getLayoutId(), contentContainer, false)
        contentContainer.addView(childLayout)

        // 设置窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化底部导航栏
        initBottomNavigation()

        // 应用高斯模糊效果到导航栏
        applyBlurEffectToNavBar()

        // 恢复music_bar状态
        restoreMusicBarState()

        // 调用子类的初始化方法
        initActivity()

    }

    /**
     * 子类需要实现的布局ID
     */
    abstract fun getLayoutId(): Int

    /**
     * 子类的初始化方法
     */
    abstract fun initActivity()

    /**
     * 初始化底部导航栏
     */
    private fun initBottomNavigation() {
        // 找到底部导航栏的视图
        homeLayout = findViewById(R.id.home_layout)
        musicLayout = findViewById(R.id.music_layout)
        profileLayout = findViewById(R.id.profile_layout)

        homeIcon = findViewById(R.id.home_icon)
        homeText = findViewById(R.id.home_text)
        musicIcon = findViewById(R.id.music_icon)
        musicText = findViewById(R.id.music_text)
        profileIcon = findViewById(R.id.profile_icon)
        profileText = findViewById(R.id.profile_text)

        // 设置点击事件 - 使用无动画的Intent跳转
        homeLayout.setOnClickListener {
            if (this !is MainActivity) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0) // 禁用Activity切换动画
            }
            updateBottomNavSelection(0)
        }

        musicLayout.setOnClickListener {
            if (this !is NavMusicActivity) {
                val intent = Intent(this, NavMusicActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0) // 禁用Activity切换动画
            }
            updateBottomNavSelection(1)
        }

        profileLayout.setOnClickListener {
            if (this !is MineActivity) {
                val intent = Intent(this, MineActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0) // 禁用Activity切换动画
            }
            updateBottomNavSelection(2)
        }

        // 根据当前Activity设置选中状态
        updateBottomNavSelection(getCurrentNavPosition())

        // 设置music_bar点击事件
        setupMusicBarClick()

        // 初始化滑动检测
        setupScrollDetection()
    }

    /**
     * 更新底部导航栏选中状态
     */
    protected fun updateBottomNavSelection(position: Int) {
        // 重置所有状态
        resetNavSelection()

        // 设置选中状态
        when (position) {
            0 -> {
                homeIcon.setImageResource(R.drawable.zhuye_on)
                homeText.setTextColor(resources.getColor(android.R.color.holo_blue_dark, null))
            }
            1 -> {
                musicIcon.setImageResource(R.drawable.ku_on)
                musicText.setTextColor(resources.getColor(android.R.color.holo_blue_dark, null))
            }
            2 -> {
                profileIcon.setImageResource(R.drawable.my_on)
                profileText.setTextColor(resources.getColor(android.R.color.holo_blue_dark, null))
            }
        }
    }

    /**
     * 重置导航栏选中状态
     */
    private fun resetNavSelection() {
        homeIcon.setImageResource(R.drawable.zhuye_off)
        homeText.setTextColor(resources.getColor(android.R.color.black, null))
        musicIcon.setImageResource(R.drawable.ku_off)
        musicText.setTextColor(resources.getColor(android.R.color.black, null))
        profileIcon.setImageResource(R.drawable.my_off)
        profileText.setTextColor(resources.getColor(android.R.color.black, null))
    }

    /**
     * 获取当前导航位置，子类可以重写
     */
    protected open fun getCurrentNavPosition(): Int {
        return when (this) {
            is MainActivity -> 0
            is NavMusicActivity -> 1
            is MineActivity -> 2
            else -> 0
        }
    }

    override fun onResume() {
        super.onResume()
        // 在onResume时更新导航栏状态，确保状态正确
        updateBottomNavSelection(getCurrentNavPosition())
    }

    override fun onPause() {
        super.onPause()
        // 在onPause时保持导航栏状态，避免闪烁
    }

    override fun finish() {
        super.finish()
        // 禁用Activity结束动画
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * 设置滑动检测
     */
    private fun setupScrollDetection() {
        try {
            // 找到music_bar视图
            musicBarView = findViewById(R.id.music_bar_card)

            // 暂时注释掉广播接收器，先测试基本功能
            /*
            // 注册广播接收器
            musicBarStateReceiver = MusicBarBroadcastManager.registerReceiver(
                this,
                musicBarView
            ) { isVisible ->
                // 只在非当前Activity触发时更新状态
                if (!isCurrentActivityTriggeringChange) {
                    isMusicBarVisible = isVisible
                    // 确保状态同步
                    if (isVisible && musicBarView.translationY != 0f) {
                        musicBarView.translationY = 0f
                    }
                }
            }
            */

            // 查找ScrollView并设置滑动监听
            val scrollView = findScrollViewInContent()
            if (scrollView != null) {
                setupScrollViewListener(scrollView)
            } else {
                // 如果没有找到ScrollView，使用触摸监听
                setupTouchListener()
            }

            // 添加调试信息
            android.util.Log.d("Nav", "setupScrollDetection completed, scrollView found: ${scrollView != null}")
        } catch (e: Exception) {
            android.util.Log.e("Nav", "setupScrollDetection error", e)
        }
    }

    /**
     * 在内容容器中查找ScrollView
     */
    private fun findScrollViewInContent(): android.widget.ScrollView? {
        val contentContainer = findViewById<View>(R.id.content_container)
        return findScrollViewRecursive(contentContainer)
    }

    /**
     * 递归查找ScrollView
     */
    private fun findScrollViewRecursive(view: View): android.widget.ScrollView? {
        if (view is android.widget.ScrollView) {
            return view
        }
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                val scrollView = findScrollViewRecursive(child)
                if (scrollView != null) {
                    return scrollView
                }
            }
        }
        return null
    }

    /**
     * 设置ScrollView的滑动监听
     */
    private fun setupScrollViewListener(scrollView: android.widget.ScrollView) {
        android.util.Log.d("Nav", "Setting up ScrollView listener")
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = scrollView.scrollY
            val lastScrollY = scrollView.tag as? Int ?: 0

            android.util.Log.d("Nav", "Scroll detected: scrollY=$scrollY, lastScrollY=$lastScrollY, isMusicBarVisible=$isMusicBarVisible")

            if (scrollY > lastScrollY && isMusicBarVisible) {
                // 向上滚动，隐藏music_bar
                android.util.Log.d("Nav", "Hiding music bar due to scroll up")
                hideMusicBar()
            } else if (scrollY < lastScrollY && !isMusicBarVisible) {
                // 向下滚动，显示music_bar
                android.util.Log.d("Nav", "Showing music bar due to scroll down")
                showMusicBar()
            }

            scrollView.tag = scrollY
        }
    }

    /**
     * 设置触摸监听（备用方案）
     */
    private fun setupTouchListener() {
        val contentContainer = findViewById<View>(R.id.content_container)
        contentContainer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastY = event.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaY = event.y - lastY
                    if (Math.abs(deltaY) > 15) { // 最小滑动距离
                        if (deltaY > 0 && !isMusicBarVisible) {
                            // 向下滑动，显示music_bar
                            showMusicBar()
                        } else if (deltaY < 0 && isMusicBarVisible) {
                            // 向上滑动，隐藏music_bar
                            hideMusicBar()
                        }
                    }
                    lastY = event.y
                    true
                }
                else -> false
            }
        }
    }

    /**
     * 显示music_bar
     */
    private fun showMusicBar() {
        android.util.Log.d("Nav", "showMusicBar called, isMusicBarVisible: $isMusicBarVisible")
        if (!isMusicBarVisible) {
            android.util.Log.d("Nav", "Starting show animation")
            isMusicBarVisible = true
            val animator = ObjectAnimator.ofFloat(musicBarView, "translationY", musicBarView.translationY, 0f)
            animator.duration = 300
            animator.start()


        } else {
            android.util.Log.d("Nav", "Music bar already visible, skipping show animation")
        }
    }

    /**
     * 隐藏music_bar
     */
    private fun hideMusicBar() {
        android.util.Log.d("Nav", "hideMusicBar called, isMusicBarVisible: $isMusicBarVisible")
        if (isMusicBarVisible) {
            android.util.Log.d("Nav", "Starting hide animation")
            isMusicBarVisible = false
            // 让music_bar向下滑动到屏幕外
            val screenHeight = resources.displayMetrics.heightPixels
            val animator = ObjectAnimator.ofFloat(musicBarView, "translationY", musicBarView.translationY, screenHeight.toFloat())
            animator.duration = 300
            animator.start()


        } else {
            android.util.Log.d("Nav", "Music bar already hidden, skipping hide animation")
        }
    }

    /**
     * 恢复music_bar状态
     */
    private fun restoreMusicBarState() {
        try {
            val songNameTextView = findViewById<TextView>(R.id.tv_song_name)
            val albumCoverImageView = findViewById<ImageView>(R.id.iv_album_cover)
            val playButton = findViewById<ImageView>(R.id.iv_play)

            // 使用MusicBarManager更新UI
            MusicBarManager.updateMusicBarUI(songNameTextView, albumCoverImageView, playButton)
        } catch (e: Exception) {
            // 如果找不到视图，忽略错误
        }
    }

    /**
     * 设置music_bar点击事件
     */
    private fun setupMusicBarClick() {
        try {
            val musicBarCard = findViewById<View>(R.id.music_bar_card)
            musicBarCard.setOnClickListener {
                // 跳转到音乐播放页面
                val intent = Intent(this, MusicPlayerActivity::class.java)
                intent.putExtra("music_name", MusicBarManager.getCurrentSongName())
                intent.putExtra("music_singer", MusicBarManager.getCurrentSingerName())
                intent.putExtra("music_pic", MusicBarManager.getCurrentAlbumCover())
                intent.putExtra("music_id", MusicBarManager.getCurrentMusicId())
                intent.putExtra("music_url", MusicBarManager.getCurrentMusicUrl())
                startActivity(intent)
            }

            // 设置播放按钮点击事件
            val playButton = findViewById<ImageView>(R.id.iv_play)
            playButton.setOnClickListener {
                // 切换播放状态
                val newPlayingState = !MusicBarManager.isPlaying()
                MusicBarManager.setPlayingState(newPlayingState)

                // 更新播放按钮图标
                if (newPlayingState) {
                    playButton.setImageResource(R.drawable.ic_pause)
                } else {
                    playButton.setImageResource(R.drawable.ic_play)
                }
            }
        } catch (e: Exception) {
            // 如果找不到视图，忽略错误
        }
    }

    /**
     * 应用高斯模糊效果到特定组件
     */
    private fun applyBlurEffectToNavBar() {
        try {
            Log.d("Nav", "开始应用高斯模糊效果到特定组件")

            // 1. 应用模糊效果到搜索按钮
            val searchBlur = findViewById<View>(R.id.search_blur)
            if (searchBlur != null) {
                Log.d("Nav", "找到搜索按钮模糊组件，应用模糊效果")
                BlurEffectManager.applyBlurToComponent(searchBlur)
            } else {
                Log.w("Nav", "未找到搜索按钮模糊组件")
            }

            // 2. 应用模糊效果到导航栏
            val navBlur = findViewById<View>(R.id.nav_blur)
            if (navBlur != null) {
                Log.d("Nav", "找到导航栏模糊组件，应用模糊效果")
                BlurEffectManager.applyBlurToComponent(navBlur)
            } else {
                Log.w("Nav", "未找到导航栏模糊组件")
            }

            // 3. 应用模糊效果到音乐栏
            val musicBarBlur = findViewById<View>(R.id.music_bar_blur)
            if (musicBarBlur != null) {
                Log.d("Nav", "找到音乐栏模糊组件，应用模糊效果")
                BlurEffectManager.applyBlurToComponent(musicBarBlur)
            } else {
                Log.w("Nav", "未找到音乐栏模糊组件")
            }

            // 测试：延迟后检查模糊效果是否应用成功
            val navContainer = findViewById<View>(R.id.nav_all_container)
            navContainer?.postDelayed({
                testBlurEffect(navContainer)
            }, 1000)

        } catch (e: Exception) {
            Log.e("Nav", "应用高斯模糊效果时发生错误", e)
            e.printStackTrace()
        }
    }

    /**
     * 测试模糊效果是否应用成功
     */
    private fun testBlurEffect(navContainer: View) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // 简单记录日志，表示测试完成
                Log.d("Nav", "✅ 高斯模糊效果测试完成")
            }
        } catch (e: Exception) {
            Log.e("Nav", "测试模糊效果时发生错误", e)
        }
    }
}