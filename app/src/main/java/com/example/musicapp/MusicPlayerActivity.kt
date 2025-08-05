package com.example.musicapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.bumptech.glide.Glide

class MusicPlayerActivity : AppCompatActivity() {
    // 视图变量（移除backString相关声明）
    lateinit var albumCover: ImageView
    lateinit var tvSongName: TextView
    lateinit var tvArtist: TextView
    lateinit var rootView: View
    lateinit var ivDown: ImageView  // iv_down图标
    lateinit var iv_play_pause : ImageView
    //处理播放播放控制栏的图标切换
//----------------------------------------------------------
    private val mainHandler = Handler(Looper.getMainLooper())
    private val playbackStateListener = object : PlaybackStateListener {
        override fun onPlaybackStateChanged(state: PlaybackState) {
            mainHandler.post {
                updatePlayButtonState(state)
            }
        }
    }
    private fun updatePlayButtonState(state: PlaybackState) {
        val resourceId = when (state) {
            PlaybackState.IDLE, PlaybackState.PAUSED, PlaybackState.ERROR -> R.drawable.ic_play
            PlaybackState.PREPARING, PlaybackState.PLAYING -> R.drawable.stop
        }

        // 强制刷新图片资源
        iv_play_pause.setImageResource(0) // 先清空
        iv_play_pause.setImageResource(resourceId) // 再设置新资源

        Log.d("PlaybackState1", "状态: $state，设置资源: $resourceId")
        Log.d("PlaybackState1", "iv_play是否初始化: ${::iv_play_pause.isInitialized}")
    }
//---------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_music_player)
        supportActionBar?.hide()

        // 绑定视图（移除backString的初始化）
        rootView = findViewById(R.id.main_player)
        albumCover = findViewById(R.id.album_cover)
        tvSongName = findViewById(R.id.tv_song_name)
        tvArtist = findViewById(R.id.tv_artist)
        ivDown = findViewById(R.id.iv_down)  // 绑定iv_down图标
        iv_play_pause = findViewById<ImageView>(R.id.iv_play_pause)



        // 读取缓存数据
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val song = prefs.getString("song", "")
        val sing = prefs.getString("sing", "")
        val pic = prefs.getString("pic_url", "").toString()
        val url = prefs.getString("music_url","").toString()

        iv_play_pause.setOnClickListener {
            stop_Or_start(url)
        }
        tvSongName.text = song
        tvArtist.text = sing
        loadAlbumCover(pic)
        registerPlaybackStateListener(playbackStateListener)

        ivDown.setOnClickListener {
            exitWithAnimation()
        }
        val currentState = getCurrentPlaybackState() // 获取当前播放状态
        playbackStateListener.onPlaybackStateChanged(currentState)
    }

    ///进入页面时触发一次监听
    private fun getCurrentPlaybackState(): PlaybackState {
        return if (getYesOrNo()) { // 假设isMusicPlaying()是判断当前是否在播放的方法
            PlaybackState.PLAYING
        } else {
            PlaybackState.PAUSED
        }
    }

    // 加载专辑封面（保留）
    private fun loadAlbumCover(picUrl: String) {
        Glide.with(this)
            .load(picUrl)
            .placeholder(R.drawable.music)
            .error(R.drawable.music)
            .into(albumCover)
    }

    // 执行退出动画并关闭Activity（保留）
    private fun exitWithAnimation() {
        val exitAnim = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)
        rootView.startAnimation(exitAnim)
        exitAnim.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                finish()
                overridePendingTransition(0, 0)
            }
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })
    }

    // 像素转DP（保留，如需其他滑动逻辑可复用）
    private fun pxToDp(px: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            px.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    // 返回键逻辑（保留）
    override fun onBackPressed() {
        exitWithAnimation()
        super.onBackPressed()
        unregisterPlaybackStateListener(playbackStateListener)
    }
}