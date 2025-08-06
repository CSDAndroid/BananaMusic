package com.example.musicapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.musicapp.network.ApiResponse
import com.example.musicapp.network.Get_Network_Music
import com.example.musicapp.network.MusicCallback
import com.example.musicapp.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NavMusicActivity : Nav() {

    private var musiclist1 = ArrayList<Music>()
    private lateinit var iv_album_cover: ImageView
    private lateinit var tv_song_name: TextView
    private lateinit var iv_play : ImageView
    private lateinit var rv_song : RecyclerView
    //处理播放播放控制栏的图标切换
//----------------------------------------------------------
    private val mainHandler = Handler(Looper.getMainLooper())
    private val playbackStateListener = object : PlaybackStateListener {
        override fun onPlaybackStateChanged(state: PlaybackState) {
            mainHandler.post {
                updatePlayButtonState(state)
            }
        }

        override fun onPlaybackTimeChanged(currentTime: Int) {

        }
    }
    private fun updatePlayButtonState(state: PlaybackState) {
        val resourceId = when (state) {
            PlaybackState.IDLE, PlaybackState.PAUSED, PlaybackState.ERROR -> R.drawable.ic_play
            PlaybackState.PREPARING, PlaybackState.PLAYING -> R.drawable.stop
        }

        // 强制刷新图片资源
        iv_play.setImageResource(0) // 先清空
        iv_play.setImageResource(resourceId) // 再设置新资源

        Log.d("PlaybackState", "状态: $state，设置资源: $resourceId")
        Log.d("PlaybackState", "iv_play是否初始化: ${::iv_play.isInitialized}")
    }
//---------------------------------------------------------------


    override fun getLayoutId(): Int {
        return R.layout.activity_nav_music
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initActivity() {

        iv_album_cover = findViewById<ImageView>(R.id.iv_album_cover)
        tv_song_name = findViewById<TextView>(R.id.tv_song_name)
        iv_play = findViewById<ImageView>(R.id.iv_play)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        val tv_song_name = findViewById<TextView>(R.id.tv_song_name)
        rv_song = findViewById<RecyclerView>(R.id.rv_song_rv)



        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        var yesOrNo = false


        iv_play.setOnClickListener {
            val url_new = prefs.getString("music_url", "") ?: ""
            val song =  prefs.getString("song", "") ?: ""
            if (yesOrNo){
                Log.d("data5", "$url_new $song")
                Log.d("data4", "$yesOrNo")
                stop_Or_start(url_new)
                iv_play.setImageResource(R.drawable.ic_play)
                yesOrNo = false
            }else{
                Log.d("data4", "$yesOrNo")
                stop_Or_start(url_new)
                iv_play.setImageResource(R.drawable.stop)
                yesOrNo = true
            }
        }
        val t = (1..4).random()
        loadmusic(t)

        swipeRefreshLayout.setOnRefreshListener{
            musiclist1.clear()
            val t = (1..4).random()
            loadmusic(t)
            swipeRefreshLayout.isRefreshing = false
        }
        val currentState = getCurrentPlaybackState() // 获取当前播放状态
        playbackStateListener.onPlaybackStateChanged(currentState)
    }

    ///进入页面时触发一次监听
    private fun getCurrentPlaybackState(): PlaybackState {
        return if (getYesOrNo()) { // 假设isMusicPlaying()是判断当前是否在播放的方法
            PlaybackState.PLAYING
        } else {
            PlaybackState.PAUSED
        }
    }

    private fun loadmusic(t : Int){
        Get_Network_Music(t,object : MusicCallback {
            override fun onSuccess(musicList: ArrayList<Music>) {
                musiclist1 = musicList
                val adapter = ContentAdapter(musiclist1,{music ->
                    loadAlbumCover(music.pic)
                    tv_song_name.text = music.song
                    getSharedPreferences("data",MODE_PRIVATE).edit{
                        putString("song","${music.song}")
                        putString("sing","${music.sing}")
                        putString("pic_url","${music.pic}")
                        putLong("music_id", music.id)
                        putString("music_url", "${music.url}")
                    }
                    val intent = Intent(this@NavMusicActivity, MusicPlayerActivity::class.java)
                    startActivity(intent)
                })
                rv_song.adapter = adapter
                val layoutmanager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                rv_song.layoutManager = layoutmanager
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(error: String) {
                TODO("Not yet implemented")
            }

        })
    }
    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "song" || key == "pic_url" || key == "sing" || key == "music_id" || key == "music_url") {
                val song = sharedPreferences.getString("song", "") ?: ""
                val pic = sharedPreferences.getString("pic_url", "") ?: ""
                val sing = sharedPreferences.getString("sing", "") ?: ""
                val musicId = sharedPreferences.getLong("music_id", 0L)
                val musicUrl = sharedPreferences.getString("music_url", "") ?: ""
                Log.e("data5", ": $musicUrl $song", )

                // 更新UI
                tv_song_name.text = "$song - $sing"
                loadAlbumCover(pic)
                // 更新MusicBarManager状态
                MusicBarManager.updateMusicInfo(
                    songName = song,
                    singerName = sing,
                    albumCover = pic,
                    musicId = musicId,
                    musicUrl = musicUrl
                )
            }
        }

    private fun loadAlbumCover(picUrl: String) {
        Glide.with(this)
            .load(picUrl) // 网络图片 URL
            .placeholder(R.drawable.fm1) // 加载中的占位图
            .error(R.drawable.fm1) // 加载失败的错误图
            .into(iv_album_cover)
    }

}