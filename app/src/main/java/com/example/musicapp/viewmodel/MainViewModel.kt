package com.example.musicapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicapp.model.entity.Music
import com.example.musicapp.commonUtils.PlaybackState
import com.example.musicapp.commonUtils.PlaybackStateManager
import com.example.musicapp.commonUtils.stop_Or_start
import com.example.musicapp.model.dataSource.remote.MusicCallback
import com.example.musicapp.model.repository.MainRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // 私有Repository实例
    private val repository: MainRepository

    // 音乐列表数据（LiveData暴露给View）
    private val _musicList1 = MutableLiveData<ArrayList<Music>>()
    val musicList1: LiveData<ArrayList<Music>> = _musicList1

    private val _musicList2 = MutableLiveData<ArrayList<Music>>()
    val musicList2: LiveData<ArrayList<Music>> = _musicList2

    // 当前播放状态
    private val playbackState: LiveData<PlaybackState> = PlaybackStateManager.playbackState

    // 当前音乐信息（歌曲名-歌手名）
    private val _currentMusicInfo = MutableLiveData<Pair<String, String>>()
    val currentMusicInfo: LiveData<Pair<String, String>> = _currentMusicInfo

    // 当前专辑封面URL
    private val _albumCoverUrl = MutableLiveData<String>()
    val albumCoverUrl: LiveData<String> = _albumCoverUrl

    init {
        // 初始化SharedPreferences和Repository
        val sharedPreferences = application.getSharedPreferences("data", Context.MODE_PRIVATE)
        repository = MainRepository(sharedPreferences)
        // 初始化时读取本地音乐信息
        loadLocalMusicInfo()
    }

    // 加载本地保存的音乐信息
    private fun loadLocalMusicInfo() {
        val (song, sing, picUrl) = repository.getCurrentMusicInfo()
        _currentMusicInfo.value = Pair(song, sing)
        _albumCoverUrl.value = picUrl
    }

    // 获取网络音乐列表（类型1）
    fun loadMusicList1() {
        repository.getNetworkMusic(1, object : MusicCallback {
            override fun onSuccess(musicList: ArrayList<Music>) {
                _musicList1.postValue(musicList)
            }

            override fun onFailure(error: String) {
                // 可通过LiveData发送错误信息给View
            }
        })
    }

    // 获取随机类型的网络音乐列表（类型2）
    fun loadRandomMusicList2() {
        val randomType = (1..4).random()
        repository.getNetworkMusic(randomType, object : MusicCallback {
            override fun onSuccess(musicList: ArrayList<Music>) {
                _musicList2.postValue(musicList)
            }

            override fun onFailure(error: String) {
                // 可通过LiveData发送错误信息给View
            }
        })
    }

    // 处理音乐项点击事件
    fun onMusicItemClick(music: Music) {
        // 保存音乐信息到本地
        repository.saveMusicInfo(music)

        PlaybackStateManager.setCurrentMusic(music)
        _currentMusicInfo.value = Pair(music.song, music.sing)
        _albumCoverUrl.value = music.pic
        PlaybackStateManager.setPlaybackState(PlaybackState.PLAYING)
    }

    // 处理播放/暂停按钮点击
    fun togglePlayPause() {
        val currentState = playbackState.value ?: PlaybackState.PAUSED
        val newState = if (currentState == PlaybackState.PLAYING) {
            PlaybackState.PAUSED
        } else {
            PlaybackState.PLAYING
        }
        PlaybackStateManager.setPlaybackState(newState)
        stop_Or_start(getCurrentMusicUrl())
    }

    // 获取当前播放的音乐URL
    fun getCurrentMusicUrl(): String {
        return repository.getCurrentMusicUrl()
    }
}