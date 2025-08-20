package com.example.musicapp.model.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.musicapp.model.entity.Music
import com.example.musicapp.model.dataSource.remote.Get_Network_Music
import com.example.musicapp.model.dataSource.remote.MusicCallback

class MainRepository(private val sharedPreferences: SharedPreferences) {

    // 获取网络音乐
    fun getNetworkMusic(type: Int, callback: MusicCallback) {
        Get_Network_Music(type, callback)
    }

    // 保存音乐信息到SharedPreferences
    fun saveMusicInfo(music: Music) {
        sharedPreferences.edit()
            .putString("song", music.song)
            .putString("sing", music.sing)
            .putString("pic_url", music.pic)
            .putLong("music_id", music.id)
            .putString("music_url", music.url)
            .apply()
    }

    // 从SharedPreferences读取当前音乐信息
    fun getCurrentMusicInfo(): Triple<String, String, String> {
        val song = sharedPreferences.getString("song", "") ?: ""
        val sing = sharedPreferences.getString("sing", "") ?: ""
        val picUrl = sharedPreferences.getString("pic_url", "") ?: ""
        return Triple(song, sing, picUrl)
    }

    // 获取当前音乐播放URL
    fun getCurrentMusicUrl(): String {
        return sharedPreferences.getString("music_url", "") ?: ""
    }
}