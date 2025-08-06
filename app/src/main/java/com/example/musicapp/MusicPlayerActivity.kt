package com.example.musicapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
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
            .load(picUrl)
            .placeholder(R.drawable.music)
            .error(R.drawable.music)
            .into(albumCover)
    }

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

    override fun onBackPressed() {
        exitWithAnimation()
        super.onBackPressed()
        unregisterPlaybackStateListener(playbackStateListener)
    }
}