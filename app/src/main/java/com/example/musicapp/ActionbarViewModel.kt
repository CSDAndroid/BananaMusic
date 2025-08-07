package com.example.musicapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

class ActionbarViewModel : ViewModel() {
    private val _actionItems = MutableLiveData<ArrayList<String>>()
    val actionItems: LiveData<ArrayList<String>> = _actionItems
    private val _selectedPosition = MutableLiveData<Int>(-1)
    val selectedPosition: LiveData<Int> = _selectedPosition

    init {
        _actionItems.value = arrayListOf(
            "原创榜", "新歌榜", "飙升榜", "热歌榜",
            "我的喜欢", "历史播放", "本地音乐"
        )
    }
}