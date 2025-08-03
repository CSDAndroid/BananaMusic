package com.example.musicapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicapp.repository.MusicRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository()
    private val _musicLists = MutableLiveData<Map<Int, List<Music>>>() // 存储4个列表
    val musicLists: LiveData<Map<Int, List<Music>>> = _musicLists

    private val _requestStatus = MutableLiveData<Boolean>() // 请求完成状态
    val requestStatus: LiveData<Boolean> = _requestStatus

    init {
        loadAllMusic()
    }

    // 加载所有音乐数据
    private fun loadAllMusic() {
        viewModelScope.launch {
            val musicMap = mutableMapOf<Int, List<Music>>()
            var requestCount = 0

            for (page in 1..4) {
                val musicList = repository.getMusic(page)
                musicMap[page] = musicList
                requestCount++

                if (requestCount == 4) {
                    _musicLists.value = musicMap
                    _requestStatus.value = true // 所有请求完成
                }
            }
        }
    }

    // 提供给UI的其他业务逻辑（如获取指定列表）
    fun getMusicList(page: Int): List<Music> {
        return _musicLists.value?.get(page) ?: emptyList()

    }
}