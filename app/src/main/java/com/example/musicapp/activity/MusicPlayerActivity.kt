package com.example.musicapp.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.musicapp.ColorExtractor
import com.example.musicapp.R
import com.example.musicapp.adapter.LyricAdapter
import com.example.musicapp.all_fun.LyricLine
import com.example.musicapp.all_fun.PlaybackState
import com.example.musicapp.all_fun.PlaybackStateListener
import com.example.musicapp.all_fun.parseLyrics
import com.example.musicapp.all_fun.registerPlaybackStateListener
import com.example.musicapp.all_fun.stop_Or_start
import com.example.musicapp.all_fun.unregisterPlaybackStateListener
import com.example.musicapp.network.Getlyric



class MusicPlayerActivity : AppCompatActivity() {
    // 视图变量
    lateinit var albumCover: ImageView
    lateinit var tvSongName: TextView
    lateinit var tvArtist: TextView
    lateinit var rootView: View
    lateinit var ivDown: ImageView
    lateinit var iv_play_pause: ImageView
    lateinit var lyrics: List<LyricLine>
    lateinit var lyric_rv: RecyclerView
    lateinit var lyricAdapter: LyricAdapter

    // 播放器相关
    private val mainHandler = Handler(Looper.getMainLooper())
    private val playbackStateListener = object : PlaybackStateListener {
        override fun onPlaybackStateChanged(state: PlaybackState) {
            mainHandler.post {
                updatePlayButtonState(state)
            }
        }

        override fun onPlaybackTimeChanged(currentTime: Int) {
            mainHandler.post {
                updateLyrics(currentTime)
            }
        }
    }

    private fun updatePlayButtonState(state: PlaybackState) {
        val resourceId = when (state) {
            PlaybackState.IDLE, PlaybackState.PAUSED, PlaybackState.ERROR -> R.drawable.ic_play
            PlaybackState.PREPARING, PlaybackState.PLAYING -> R.drawable.stop
        }

        iv_play_pause.setImageResource(resourceId)
        Log.d("PlaybackState1", "状态: $state，设置资源: $resourceId")
    }

    private fun updateLyrics(currentTime: Int) {
        val currentLineIndex = lyrics.indexOfFirst { it.time > currentTime } - 1
        if (currentLineIndex in lyrics.indices) {
            lyricAdapter.setCurrentLineIndex(currentLineIndex)
            lyric_rv.scrollToPosition(currentLineIndex)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_music_player)
        supportActionBar?.hide()

        // 绑定视图
        rootView = findViewById(R.id.main_player)
        albumCover = findViewById(R.id.album_cover)
        tvSongName = findViewById(R.id.tv_song_name)
        tvArtist = findViewById(R.id.tv_artist)
        ivDown = findViewById(R.id.iv_down)
        iv_play_pause = findViewById(R.id.iv_play_pause)
        lyric_rv = findViewById(R.id.lyric_rv)

        // 读取缓存数据
        val prefs = getSharedPreferences("data", MODE_PRIVATE)
        val song = prefs.getString("song", "")
        val sing = prefs.getString("sing", "")
        val id = prefs.getLong("music_id", 1456890009).toString()
        val pic = prefs.getString("pic_url", "").toString()
        val url = prefs.getString("music_url", "").toString()

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

        // 获取歌词
        Getlyric.getlyric(id) { lyric ->
            runOnUiThread {
                val lyricsText = lyric
                lyrics = parseLyrics(lyricsText)
                lyric_rv.layoutManager = LinearLayoutManager(this)
                lyricAdapter = LyricAdapter(lyrics)
                lyric_rv.adapter = lyricAdapter
            }
        }
    }

    private fun loadAlbumCover(picUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(picUrl)
            .placeholder(R.drawable.music)
            .error(R.drawable.music)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // 设置专辑封面
                    albumCover.setImageBitmap(resource)
                    
                    // 提取主色调并应用到背景
                    applyColorToBackground(resource)
                }
            })
    }
    
    /**
     * 从专辑封面提取颜色并应用到背景
     */
    private fun applyColorToBackground(bitmap: Bitmap) {
        try {
            // 提取主色调
            val dominantColor = ColorExtractor.extractDominantColor(bitmap)
            
            // 创建简单的纯色背景（浅色系）
            val backgroundDrawable = GradientDrawable()
            backgroundDrawable.shape = GradientDrawable.RECTANGLE
            backgroundDrawable.setColor(dominantColor)
            backgroundDrawable.cornerRadius = 0f // 无圆角
            
            // 应用背景到根视图，带动画效果
            applyBackgroundWithAnimation(backgroundDrawable)
            
            Log.d("MusicPlayerActivity", "背景颜色已更新: ${String.format("#%06X", dominantColor)}")
            
        } catch (e: Exception) {
            Log.e("MusicPlayerActivity", "应用背景颜色时发生错误", e)
            // 如果出错，使用默认背景
            rootView.setBackgroundResource(R.drawable.frosted_glass)
        }
    }
    
    /**
     * 带动画效果应用背景
     */
    private fun applyBackgroundWithAnimation(newGradient: GradientDrawable) {
        // 创建透明度动画
        val alphaAnimator = ValueAnimator.ofFloat(0f, 1f)
        alphaAnimator.duration = 800 // 800毫秒
        
        alphaAnimator.addUpdateListener { animator ->
            val alpha = animator.animatedValue as Float
            newGradient.alpha = (alpha * 255).toInt()
            
            // 在动画过程中应用背景
            if (rootView.background == null) {
                rootView.background = newGradient
            }
        }
        
        alphaAnimator.start()
    }

    private fun exitWithAnimation() {
        val exitAnim = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)
        rootView.startAnimation(exitAnim)
        exitAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                finish()
                overridePendingTransition(0, 0)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    override fun onBackPressed() {
        exitWithAnimation()
        super.onBackPressed()
        unregisterPlaybackStateListener(playbackStateListener)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 清理资源
        mainHandler.removeCallbacksAndMessages(null)
    }
}