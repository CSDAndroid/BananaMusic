package com.example.musicapp.commonUtils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicapp.model.entity.Music
import com.example.musicapp.model.repository.MainRepository

object PlaybackStateManager {
    private val _playbackState = MutableLiveData<PlaybackState>()
    val playbackState: LiveData<PlaybackState> = _playbackState

    private val _currentMusic = MutableLiveData<Music?>()
    val currentMusic: LiveData<Music?> = _currentMusic

    fun setPlaybackState(state: PlaybackState) {
        _playbackState.postValue(state)
        // 同步到全局状态
        when (state) {
            PlaybackState.PLAYING -> setYesOrNo(true)
            else -> setYesOrNo(false)
        }
    }

    private lateinit var repository: MainRepository

    fun setCurrentMusic(music: Music) {
        _currentMusic.postValue(music)
        // 保存到SharedPreferences
        if (::repository.isInitialized) { // 确保repository已初始化
            repository.saveMusicInfo(music)
        }
    }


    fun init(context: Context) {
        if (!::repository.isInitialized) {
            val prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE)
            repository = MainRepository(prefs)
        }

        // 从SharedPreferences恢复播放状态
        val isPlaying = getYesOrNo()
        val state = if (isPlaying) PlaybackState.PLAYING else PlaybackState.PAUSED
        _playbackState.postValue(state)
    }

    // 添加获取当前状态的方法
    fun getCurrentState(): PlaybackState {
        return _playbackState.value ?: PlaybackState.PAUSED
    }
}