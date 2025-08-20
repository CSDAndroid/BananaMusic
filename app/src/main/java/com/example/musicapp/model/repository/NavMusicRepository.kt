package com.example.musicapp.model.repository

import com.example.musicapp.model.dataSource.remote.Get_Network_Music
import com.example.musicapp.model.dataSource.remote.MusicCallback

class NavMusicRepository {
    fun getMusic(t: Int, callback: MusicCallback) {
        Get_Network_Music(t, callback) // 封装网络请求
    }
}
