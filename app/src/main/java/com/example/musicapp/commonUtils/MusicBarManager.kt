package com.example.musicapp.commonUtils

import android.content.Context
import android.content.SharedPreferences
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.musicapp.R

/** 音乐栏状态管理器 - 单例模式
 * 用于在Activity切换时保持music_bar的状态
 */
object MusicBarManager {
    
    private const val PREF_NAME = "music_bar_state"
    private const val KEY_SONG_NAME = "song_name"
    private const val KEY_SINGER_NAME = "singer_name"
    private const val KEY_ALBUM_COVER = "album_cover"
    private const val KEY_MUSIC_ID = "music_id"
    private const val KEY_MUSIC_URL = "music_url"
    private const val KEY_IS_PLAYING = "is_playing"
    
    private var sharedPreferences: SharedPreferences? = null
    
    // 当前音乐信息
    private var currentSongName: String = ""
    private var currentSingerName: String = ""
    private var currentAlbumCover: String = ""
    private var currentMusicId: Long = 0L
    private var currentMusicUrl: String = ""
    private var isPlaying: Boolean = false
    
    /*** 初始化管理器*/
    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            loadState()
        }
    }
    
    /*** 更新音乐信息*/
    fun updateMusicInfo(
        songName: String,
        singerName: String,
        albumCover: String,
        musicId: Long,
        musicUrl: String
    ) {
        currentSongName = songName
        currentSingerName = singerName
        currentAlbumCover = albumCover
        currentMusicId = musicId
        currentMusicUrl = musicUrl
        isPlaying = PlaybackStateManager.playbackState.value == PlaybackState.PLAYING
        saveState()
    }
    
    /** 设置播放状态*/
    fun setPlayingState(playing: Boolean) {
        isPlaying = playing
        saveState()
    }
    
    /*** 获取当前歌曲名称*/
    fun getCurrentSongName(): String = currentSongName
    
    /*** 获取当前歌手名称*/
    fun getCurrentSingerName(): String = currentSingerName
    
    /*** 获取当前专辑封面URL*/
    fun getCurrentAlbumCover(): String = currentAlbumCover
    
    /*** 获取当前音乐ID*/
    fun getCurrentMusicId(): Long = currentMusicId
    
    /*** 获取当前音乐URL*/
    fun getCurrentMusicUrl(): String = currentMusicUrl
    
    /*** 获取播放状态*/
    fun isPlaying(): Boolean = isPlaying
    
    /*** 更新music_bar的UI*/
    fun updateMusicBarUI(
        songNameTextView: TextView,
        albumCoverImageView: ImageView,
        playButton: ImageView? = null
    ) {
        // 更新歌曲名称
        songNameTextView.text = currentSongName
        
        // 更新专辑封面
        if (currentAlbumCover.isNotEmpty()) {
            Glide.with(songNameTextView.context)
                .load(currentAlbumCover)
                .placeholder(R.drawable.fm1)
                .error(R.drawable.fm1)
                .into(albumCoverImageView)
        } else {
            albumCoverImageView.setImageResource(R.drawable.fm1)
        }
        
        // 更新播放按钮状态
        playButton?.let {
            if (isPlaying) {
                it.setImageResource(R.drawable.ic_pause)
            } else {
                it.setImageResource(R.drawable.ic_play)
            }
        }
    }
    
    /*** 保存状态到SharedPreferences*/
    private fun saveState() {
        sharedPreferences?.edit()?.apply {
            putString(KEY_SONG_NAME, currentSongName)
            putString(KEY_SINGER_NAME, currentSingerName)
            putString(KEY_ALBUM_COVER, currentAlbumCover)
            putLong(KEY_MUSIC_ID, currentMusicId)
            putString(KEY_MUSIC_URL, currentMusicUrl)
            putBoolean(KEY_IS_PLAYING, isPlaying)
            apply()
        }
    }
    
    /*** 从SharedPreferences加载状态*/
    private fun loadState() {
        sharedPreferences?.let { prefs ->
            currentSongName = prefs.getString(KEY_SONG_NAME, "") ?: ""
            currentSingerName = prefs.getString(KEY_SINGER_NAME, "") ?: ""
            currentAlbumCover = prefs.getString(KEY_ALBUM_COVER, "") ?: ""
            currentMusicId = prefs.getLong(KEY_MUSIC_ID, 0L)
            currentMusicUrl = prefs.getString(KEY_MUSIC_URL, "") ?: ""
            isPlaying = prefs.getBoolean(KEY_IS_PLAYING, false)
        }
    }
    
    /*** 清除状态*/
    fun clearState() {
        currentSongName = ""
        currentSingerName = ""
        currentAlbumCover = ""
        currentMusicId = 0L
        currentMusicUrl = ""
        isPlaying = false
        saveState()
    }
} 