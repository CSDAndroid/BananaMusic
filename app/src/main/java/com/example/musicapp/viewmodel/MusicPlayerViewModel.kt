// MusicPlayerViewModel.kt
package com.example.musicapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.graphics.Bitmap
import android.util.Log
import com.example.musicapp.commonUtils.ColorExtractor
import com.example.musicapp.commonUtils.PlaybackState
import com.example.musicapp.commonUtils.stop_Or_start
import com.example.musicapp.model.dataSource.remote.Getlyric
import com.example.musicapp.model.entity.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicPlayerViewModel : ViewModel() {

    // 播放状态
    private val _playbackState = MutableLiveData<PlaybackState>()
    val playbackState: LiveData<PlaybackState> = _playbackState

    // 当前播放时间
    private val _currentTime = MutableLiveData<Int>()
    val currentTime: LiveData<Int> = _currentTime

    // 歌曲信息 (song, singer, pic)
    private val _songInfo = MutableLiveData<Triple<String, String, String>>()
    val songInfo: LiveData<Triple<String, String, String>> = _songInfo

    // 歌词
    private val _lyrics = MutableLiveData<String>()
    val lyrics: LiveData<String> = _lyrics

    // 背景颜色
    private val _backgroundColor = MutableLiveData<Int>()
    val backgroundColor: LiveData<Int> = _backgroundColor

    // 当前播放的URL
    private var currentMusicUrl: String = ""
    private val _currentMusicUrl = MutableLiveData<String>()

    fun setSongInfo(song: String, singer: String, pic: String) {
        _songInfo.value = Triple(song, singer, pic)
    }

    fun updatePlaybackState(state: PlaybackState) {
        _playbackState.value = state
    }

    fun updatePlaybackTime(time: Int) {
        _currentTime.value = time
    }

    fun togglePlayback() {
        val url = _currentMusicUrl.value ?: ""
        if (url.isEmpty()) {
            Log.e("MusicPlayerVM", "播放URL为空，无法操作")
            return
        }
        stop_Or_start(url) // 使用ViewModel中存储的URL
    }

    fun loadLyrics(songId: String) {
        Getlyric.getlyric(songId) { lyric ->
            _lyrics.postValue(lyric)
        }
    }

    fun extractBackgroundColor(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.Default).launch {
            val color = ColorExtractor.extractDominantColor(bitmap)
            _backgroundColor.postValue(color)
        }
    }
    fun setCurrentMusicUrl(url: String) {
        _currentMusicUrl.value = url
    }
}