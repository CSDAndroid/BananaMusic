// NavMusicViewModel.kt
package com.example.musicapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.model.dataSource.remote.MusicCallback
import com.example.musicapp.model.entity.Music
import com.example.musicapp.model.repository.NavMusicRepository

class NavMusicViewModel : ViewModel() {
    private val repository = NavMusicRepository()
    private val _musicList = MutableLiveData<List<Music>>()
    val musicList: LiveData<List<Music>> get() = _musicList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadMusic(t: Int) {
        _isLoading.value = true
        repository.getMusic(t, object : MusicCallback { // 通过Repository获取数据
            override fun onSuccess(musicList: ArrayList<Music>) {
                _musicList.value = musicList
                _isLoading.value = false
            }
            override fun onFailure(error: String) {
                _error.value = error
                _isLoading.value = false
            }
        })
    }
}