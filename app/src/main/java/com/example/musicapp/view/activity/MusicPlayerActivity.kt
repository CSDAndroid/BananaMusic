package com.example.musicapp.view.activity

import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.musicapp.commonUtils.ColorExtractor
import com.example.musicapp.R
import com.example.musicapp.view.adapter.LyricAdapter
import com.example.musicapp.view.adapter.TitleAdapter
import com.example.musicapp.model.entity.LyricLine
import com.example.musicapp.commonUtils.PlaybackState
import com.example.musicapp.commonUtils.PlaybackStateListener
import com.example.musicapp.commonUtils.PlaybackStateManager
import com.example.musicapp.commonUtils.getPlaybackState
import com.example.musicapp.model.entity.parseLyrics
import com.example.musicapp.commonUtils.registerPlaybackStateListener
import com.example.musicapp.commonUtils.stop_Or_start
import com.example.musicapp.commonUtils.unregisterPlaybackStateListener
import com.example.musicapp.model.dataSource.remote.Get_Network_Music
import com.example.musicapp.model.dataSource.remote.Getlyric
import com.example.musicapp.model.dataSource.remote.MusicCallback
import com.example.musicapp.model.entity.Music
import com.example.musicapp.viewmodel.MusicPlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class MusicPlayerActivity : AppCompatActivity() {

    private val viewModel: MusicPlayerViewModel by viewModels()
    private lateinit var albumCover: ImageView
    private lateinit var tvSongName: TextView
    private lateinit var tvArtist: TextView
    private lateinit var rootView: View
    private lateinit var ivDown: ImageView
    private lateinit var iv_play_pause: ImageView
    private lateinit var lyrics: List<LyricLine>
    private lateinit var lyric_rv: RecyclerView
    private lateinit var lyricAdapter: LyricAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetRecyclerView: RecyclerView


    private val playbackStateListener = object : PlaybackStateListener {
        override fun onPlaybackStateChanged(state: PlaybackState) {
            viewModel.updatePlaybackState(state)
        }

        override fun onPlaybackTimeChanged(currentTime: Int) {
            viewModel.updatePlaybackTime(currentTime)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_music_player)
        supportActionBar?.hide()

        initViews()
        setupViewModelObservers()
        setupBottomSheet()
        loadInitialData()


        PlaybackStateManager.playbackState.observe(this) { state ->
            viewModel.updatePlaybackState(state)

            updatePlayButtonState(state) // 直接更新UI
        }
        registerPlaybackStateListener(playbackStateListener)
        updatePlayButtonState(getPlaybackState())
    }

    private fun initViews() {
        rootView = findViewById(R.id.main_player)
        albumCover = findViewById(R.id.album_cover)
        tvSongName = findViewById(R.id.tv_song_name)
        tvArtist = findViewById(R.id.tv_artist)
        ivDown = findViewById(R.id.iv_down)
        iv_play_pause = findViewById(R.id.iv_play_pause)
        lyric_rv = findViewById(R.id.lyric_rv)

        iv_play_pause.setOnClickListener {
            viewModel.togglePlayback()
        }

        ivDown.setOnClickListener {
            exitWithAnimation()
        }
    }

    private fun setupViewModelObservers() {
        viewModel.playbackState.observe(this) { state ->
            updatePlayButtonState(state)
        }

        viewModel.currentTime.observe(this) { time ->
            updateLyrics(time)
        }

        viewModel.songInfo.observe(this) { (song, singer, pic) ->
            tvSongName.text = song
            tvArtist.text = singer
            loadAlbumCover(pic)
        }

        viewModel.lyrics.observe(this) { lyricsText ->
            lyrics = parseLyrics(lyricsText)
            lyric_rv.layoutManager = LinearLayoutManager(this)
            lyricAdapter = LyricAdapter(lyrics)
            lyric_rv.adapter = lyricAdapter
        }

        viewModel.backgroundColor.observe(this) { color ->
            applyColorToBackground(color)
        }
    }

    private fun setupBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetRecyclerView = bottomSheetView.findViewById(R.id.bottom_sheet_recycler_view)
        bottomSheetDialog.setContentView(bottomSheetView)

        findViewById<ImageView>(R.id.iv_audio_effect).setOnClickListener {
            bottomSheetDialog.show()
            loadMusicList()
        }
    }

    private fun loadInitialData() {
        val prefs = getSharedPreferences("data", MODE_PRIVATE)
        val song = prefs.getString("song", "") ?: ""
        val sing = prefs.getString("sing", "") ?: ""
        val pic = prefs.getString("pic_url", "") ?: ""
        val id = prefs.getLong("music_id", 1456890009).toString()
        val url = prefs.getString("music_url", "") ?: "" // 获取URL

        viewModel.setSongInfo(song, sing, pic)
        viewModel.setCurrentMusicUrl(url)
        viewModel.loadLyrics(id)
    }

    private fun loadMusicList() {
        Get_Network_Music(2, object : MusicCallback {
            override fun onSuccess(musicList: ArrayList<Music>) {
                val adapter = TitleAdapter(musicList) { music ->
                    updateMusicInfo(music)
                    bottomSheetDialog.dismiss()
                }
                bottomSheetRecyclerView.layoutManager = LinearLayoutManager(this@MusicPlayerActivity)
                bottomSheetRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(error: String) {
                Log.e("error1", "onFailure: $error")
            }
        })
    }

    private fun updateMusicInfo(music: Music) {
        // 更新SharedPreferences
        getSharedPreferences("data", MODE_PRIVATE).edit {
            putString("song", music.song)
            putString("sing", music.sing)
            putString("pic_url", music.pic)
            putLong("music_id", music.id)
            putString("music_url", music.url)
        }

        viewModel.setCurrentMusicUrl(music.url)
        viewModel.setSongInfo(music.song, music.sing, music.pic)
        viewModel.loadLyrics(music.id.toString())
    }

    private fun loadAlbumCover(picUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(picUrl)
            .placeholder(R.drawable.music)
            .error(R.drawable.music)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    albumCover.setImageBitmap(resource)
                    viewModel.extractBackgroundColor(resource)
                }
            })
    }

    private fun updatePlayButtonState(state: PlaybackState) {
        val resourceId = when (state) {
            PlaybackState.IDLE, PlaybackState.PAUSED, PlaybackState.ERROR -> R.drawable.ic_play
            PlaybackState.PREPARING, PlaybackState.PLAYING -> R.drawable.stop
        }
        iv_play_pause.setImageResource(resourceId)
    }


    private fun updateLyrics(currentTime: Int) {
        val currentLineIndex = lyrics.indexOfFirst { it.time > currentTime } - 1
        if (currentLineIndex in lyrics.indices) {
            lyricAdapter.setCurrentLineIndex(currentLineIndex)
            lyric_rv.scrollToPosition(currentLineIndex)
        }
    }

    private fun applyColorToBackground(dominantColor: Int) {
        try {
            val backgroundDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(dominantColor)
                cornerRadius = 0f
            }
            applyBackgroundWithAnimation(backgroundDrawable)
        } catch (e: Exception) {
            rootView.setBackgroundResource(R.drawable.frosted_glass)
        }
    }

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
        unregisterPlaybackStateListener(playbackStateListener)
    }

}