package com.example.musicapp

import android.content.Context
import android.os.Bundle
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

        // 读取缓存数据
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val song = prefs.getString("song", "")
        val sing = prefs.getString("sing", "")
        val pic = prefs.getString("pic_url", "").toString()
        tvSongName.text = song
        tvArtist.text = sing
        loadAlbumCover(pic)

        // 移除backString的触摸事件监听逻辑

        // 移除backString的点击事件监听逻辑

        // iv_down图标的点击事件（保留）
        ivDown.setOnClickListener {
            exitWithAnimation()
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
    }
}