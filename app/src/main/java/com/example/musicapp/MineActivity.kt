package com.example.musicapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class MineActivity : Nav() {
    private lateinit var iv_play : ImageView
    private lateinit var tv_song_name : TextView
    private lateinit var iv_album_cover : ImageView
    //处理播放播放控制栏的图标切换
//----------------------------------------------------------
    private val mainHandler = Handler(Looper.getMainLooper())
    private val playbackStateListener = object : PlaybackStateListener {
        override fun onPlaybackStateChanged(state: PlaybackState) {
            mainHandler.post {
                updatePlayButtonState(state)
            }
        }

        override fun onPlaybackTimeChanged(currentTime: Int) {
        }
    }
    private fun updatePlayButtonState(state: PlaybackState) {
        val resourceId = when (state) {
            PlaybackState.IDLE, PlaybackState.PAUSED, PlaybackState.ERROR -> R.drawable.ic_play
            PlaybackState.PREPARING, PlaybackState.PLAYING -> R.drawable.stop
        }

        // 强制刷新图片资源
        iv_play.setImageResource(0) // 先清空
        iv_play.setImageResource(resourceId) // 再设置新资源

        Log.d("PlaybackState", "状态: $state，设置资源: $resourceId")
        Log.d("PlaybackState", "iv_play是否初始化: ${::iv_play.isInitialized}")
    }
    //---------------------------------------------------------------
    override fun getLayoutId(): Int {
        return R.layout.activity_mine
    }

    override fun initActivity() {
        // 在这里初始化MineActivity特有的功能
        // 例如：设置标题、初始化视图等
        iv_play = findViewById<ImageView>(R.id.iv_play)
        tv_song_name = findViewById<TextView>(R.id.tv_song_name)
        iv_album_cover = findViewById<ImageView>(R.id.iv_album_cover)
        registerPlaybackStateListener(playbackStateListener)






        // 从SharedPreferences中读取数据
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        val song = prefs.getString("song", "") ?: ""
        val sing = prefs.getString("sing", "1") ?: ""
        val pic = prefs.getString("pic_url", "") ?: ""
        val url = prefs.getString("music_url", "") ?: ""

        iv_play.setOnClickListener {
            val url_new = prefs.getString("music_url", "") ?: ""
            stop_Or_start(url_new)
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
    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "song" || key == "pic_url" || key == "sing" || key == "music_id" || key == "music_url") {
                val song = sharedPreferences.getString("song", "") ?: ""
                val pic = sharedPreferences.getString("pic_url", "") ?: ""
                val sing = sharedPreferences.getString("sing", "") ?: ""
                val musicId = sharedPreferences.getLong("music_id", 0L)
                val musicUrl = sharedPreferences.getString("music_url", "") ?: ""
                Log.e("data5", ": $musicUrl $song", )

                // 更新UI
                tv_song_name.text = "$song - $sing"
                loadAlbumCover(pic)
                // 更新MusicBarManager状态
                MusicBarManager.updateMusicInfo(
                    songName = song,
                    singerName = sing,
                    albumCover = pic,
                    musicId = musicId,
                    musicUrl = musicUrl
                )
            }
        }
    private fun loadAlbumCover(picUrl: String) {
        Glide.with(this)
            .load(picUrl) // 网络图片 URL
            .placeholder(R.drawable.fm1) // 加载中的占位图
            .error(R.drawable.fm1) // 加载失败的错误图
            .into(iv_album_cover)
    }
}