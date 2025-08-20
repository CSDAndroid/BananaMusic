// NavMusicActivity.kt
package com.example.musicapp.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.commonUtils.MusicBarManager
import com.example.musicapp.commonUtils.PlaybackState
import com.example.musicapp.commonUtils.PlaybackStateListener
import com.example.musicapp.commonUtils.getYesOrNo
import com.example.musicapp.commonUtils.stop_Or_start
import com.example.musicapp.view.adapter.ContentAdapter
import com.example.musicapp.view.navigation.Nav
import com.example.musicapp.viewmodel.NavMusicViewModel

class NavMusicActivity : Nav() {

    private val viewModel: NavMusicViewModel by viewModels()
    private lateinit var iv_album_cover: ImageView
    private lateinit var tv_song_name: TextView
    private lateinit var iv_play : ImageView
    private lateinit var rv_song : RecyclerView
    private lateinit var adapter: ContentAdapter

    // 处理播放播放控制栏的图标切换
    private val mainHandler = Handler(Looper.getMainLooper())
    private val playbackStateListener = object : PlaybackStateListener {
        override fun onPlaybackStateChanged(state: PlaybackState) {
            mainHandler.post {
                updatePlayButtonState(state)
            }
        }

        override fun onPlaybackTimeChanged(currentTime: Int) {}
    }

    private fun updatePlayButtonState(state: PlaybackState) {
        val resourceId = when (state) {
            PlaybackState.IDLE, PlaybackState.PAUSED, PlaybackState.ERROR -> R.drawable.ic_play
            PlaybackState.PREPARING, PlaybackState.PLAYING -> R.drawable.stop
        }

        // 强制刷新图片资源
        if (::iv_play.isInitialized) {
            iv_play.setImageResource(0) // 先清空
            iv_play.setImageResource(resourceId) // 再设置新资源
        }

        Log.d("PlaybackState", "状态: $state，设置资源: $resourceId")
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_nav_music
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initActivity() {
        iv_album_cover = findViewById(R.id.iv_album_cover)
        tv_song_name = findViewById(R.id.tv_song_name)
        iv_play = findViewById(R.id.iv_play)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        rv_song = findViewById(R.id.rv_song_rv)

        val prefs = getSharedPreferences("data", MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)

        iv_play.setOnClickListener {
            val urlNew = prefs.getString("music_url", "") ?: ""
            if (urlNew.isEmpty()) {
                Log.e("NavMusic", "播放URL为空，无法播放")
                return@setOnClickListener // 避免空URL导致崩溃
            }
            // 使用全局状态判断，替换本地yesOrNo
            val isPlaying = getYesOrNo()
            if (isPlaying) {
                stop_Or_start(urlNew)
                iv_play.setImageResource(R.drawable.ic_play)
            } else {
                stop_Or_start(urlNew)
                iv_play.setImageResource(R.drawable.stop)
            }
        }

        // 初始化适配器
        adapter = ContentAdapter { music ->
            loadAlbumCover(music.pic)
            tv_song_name.text = music.song
            getSharedPreferences("data", MODE_PRIVATE).edit {
                putString("song", music.song)
                putString("sing", music.sing)
                putString("pic_url", music.pic)
                putLong("music_id", music.id)
                putString("music_url", music.url)
            }
            startActivity(Intent(this@NavMusicActivity, MusicPlayerActivity::class.java))
        }

        rv_song.adapter = adapter
        rv_song.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // 观察ViewModel的数据
        viewModel.musicList.observe(this) { musicList ->
            adapter.submitList(musicList)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.error.observe(this) { error ->
            Log.d("NavMusicActivity", "Error: $error")
        }

        // 初始加载数据
        viewModel.loadMusic((1..4).random())

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadMusic((1..4).random())
        }

        val currentState = getCurrentPlaybackState()
        playbackStateListener.onPlaybackStateChanged(currentState)
    }

    private fun getCurrentPlaybackState(): PlaybackState {
        return if (getYesOrNo()) PlaybackState.PLAYING else PlaybackState.PAUSED
    }

    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "song" || key == "pic_url" || key == "sing" || key == "music_id" || key == "music_url") {
                val song = sharedPreferences.getString("song", "") ?: ""
                val pic = sharedPreferences.getString("pic_url", "") ?: ""
                val sing = sharedPreferences.getString("sing", "") ?: ""
                val musicId = sharedPreferences.getLong("music_id", 0L)
                val musicUrl = sharedPreferences.getString("music_url", "") ?: ""

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
            .load(picUrl)
            .placeholder(R.drawable.fm1)
            .error(R.drawable.fm1)
            .into(iv_album_cover)
    }
}


