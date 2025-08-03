package com.example.musicapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

class ActionbarViewModel : ViewModel() {
    // 导航栏选项数据（替代原addActionselect()方法）
    private val _actionItems = MutableLiveData<ArrayList<String>>()
    val actionItems: LiveData<ArrayList<String>> = _actionItems

    // 选中位置状态（用LiveData保存，支持配置变化后恢复）
    private val _selectedPosition = MutableLiveData<Int>(-1)
    val selectedPosition: LiveData<Int> = _selectedPosition

    init {
        _actionItems.value = arrayListOf(
            "原创榜", "新歌榜", "飙升榜", "热歌榜",
            "我的喜欢", "历史播放", "本地音乐"
        )
    }

    // 更新选中位置（供UI层调用）
    fun updateSelectedPosition(position: Int) {
        _selectedPosition.value = position
    }
}