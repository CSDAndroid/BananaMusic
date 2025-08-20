package com.example.musicapp.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.commonUtils.PlaybackState
import com.example.musicapp.view.navigation.Nav
import com.example.musicapp.viewmodel.MineViewModel

class MineActivity : Nav() {
    private lateinit var ivPlay: ImageView
    private lateinit var tvSongName: TextView
    private lateinit var ivAlbumCover: ImageView
    private lateinit var viewModel: MineViewModel

    override fun getLayoutId(): Int {
        return R.layout.activity_mine
    }

    override fun initActivity() {
        // 初始化ViewModel
        viewModel = ViewModelProvider(this)[MineViewModel::class.java]

        // 初始化视图
        initViews()

        // 观察数据变化
        observeViewModel()

        // 设置点击事件
        setupClickListeners()
    }

    private fun initViews() {
        ivPlay = findViewById(R.id.iv_play)
        tvSongName = findViewById(R.id.tv_song_name)
        ivAlbumCover = findViewById(R.id.iv_album_cover)
    }

    private fun observeViewModel() {
        // 观察歌曲信息
        viewModel.currentSong.observe(this) { song ->
            updateSongInfoDisplay(song, viewModel.currentSinger.value ?: "")
        }

        viewModel.currentSinger.observe(this) { singer ->
            updateSongInfoDisplay(viewModel.currentSong.value ?: "", singer)
        }

        // 观察专辑封面
        viewModel.currentPicUrl.observe(this) { picUrl ->
            loadAlbumCover(picUrl)
        }

        // 观察播放状态
        viewModel.playbackState.observe(this) { state ->
            updatePlayButtonState(state)
        }
    }

    private fun setupClickListeners() {
        ivPlay.setOnClickListener {
            viewModel.togglePlayPause()
        }
    }

    private fun updateSongInfoDisplay(song: String, singer: String) {
        tvSongName.text = "$song - $singer"
    }

    private fun updatePlayButtonState(state: PlaybackState) {
        val resourceId = when (state) {
            PlaybackState.IDLE, PlaybackState.PAUSED, PlaybackState.ERROR -> R.drawable.ic_play
            PlaybackState.PREPARING, PlaybackState.PLAYING -> R.drawable.stop
        }

        // 强制刷新图片资源
        ivPlay.setImageResource(0) // 先清空
        ivPlay.setImageResource(resourceId) // 再设置新资源

        Log.d("PlaybackState", "状态: $state，设置资源: $resourceId")
        Log.d("PlaybackState", "iv_play是否初始化: ${::ivPlay.isInitialized}")
    }

    private fun loadAlbumCover(picUrl: String) {
        Glide.with(this)
            .load(picUrl) // 网络图片 URL
            .placeholder(R.drawable.fm1) // 加载中的占位图
            .error(R.drawable.fm1) // 加载失败的错误图
            .into(ivAlbumCover)
    }
}