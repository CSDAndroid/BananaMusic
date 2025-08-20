package com.example.musicapp.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicapp.commonUtils.MusicBarManager
import com.example.musicapp.commonUtils.PlaybackState
import com.example.musicapp.commonUtils.PlaybackStateListener
import com.example.musicapp.commonUtils.registerPlaybackStateListener
import com.example.musicapp.commonUtils.stop_Or_start
import com.example.musicapp.commonUtils.getYesOrNo

class MineViewModel(application: Application) : AndroidViewModel(application) {
    // 私有数据
    private val _currentSong = MutableLiveData("")
    private val _currentSinger = MutableLiveData("")
    private val _currentPicUrl = MutableLiveData("")
    private val _currentMusicUrl = MutableLiveData("")
    private val _currentMusicId = MutableLiveData(0L)
    private val _playbackState = MutableLiveData<PlaybackState>(PlaybackState.PAUSED)

    // 暴露给View的LiveData
    val currentSong: LiveData<String> = _currentSong
    val currentSinger: LiveData<String> = _currentSinger
    val currentPicUrl: LiveData<String> = _currentPicUrl
    val playbackState: LiveData<PlaybackState> = _playbackState

    // SharedPreferences
    private val prefs: SharedPreferences = application.getSharedPreferences("data", 0)
    private val sharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key in listOf("song", "pic_url", "sing", "music_id", "music_url")) {
            updateMusicInfoFromPrefs(sharedPreferences)
        }
    }

    // 播放状态监听器
    private val playbackStateListener = object : PlaybackStateListener {
        override fun onPlaybackStateChanged(state: PlaybackState) {
            _playbackState.postValue(state)
        }

        override fun onPlaybackTimeChanged(currentTime: Int) {}
    }

    init {
        // 初始化数据
        updateMusicInfoFromPrefs(prefs)
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        registerPlaybackStateListener(playbackStateListener)
        initCurrentPlaybackState()
    }

    // 从SharedPreferences更新音乐信息
    private fun updateMusicInfoFromPrefs(sharedPreferences: SharedPreferences) {
        val song = sharedPreferences.getString("song", "") ?: ""
        val sing = sharedPreferences.getString("sing", "") ?: ""
        val pic = sharedPreferences.getString("pic_url", "") ?: ""
        val musicId = sharedPreferences.getLong("music_id", 0L)
        val musicUrl = sharedPreferences.getString("music_url", "") ?: ""

        _currentSong.postValue(song)
        _currentSinger.postValue(sing)
        _currentPicUrl.postValue(pic)
        _currentMusicId.postValue(musicId)
        _currentMusicUrl.postValue(musicUrl)

        // 更新MusicBarManager
        MusicBarManager.updateMusicInfo(
            songName = song,
            singerName = sing,
            albumCover = pic,
            musicId = musicId,
            musicUrl = musicUrl
        )
    }

    // 初始化当前播放状态
    private fun initCurrentPlaybackState() {
        val state = if (getYesOrNo()) PlaybackState.PLAYING else PlaybackState.PAUSED
        _playbackState.postValue(state)
    }

    // 处理播放/暂停点击
    fun togglePlayPause() {
        val url = _currentMusicUrl.value ?: ""
        stop_Or_start(url)
    }

    override fun onCleared() {
        super.onCleared()
        prefs.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }
}