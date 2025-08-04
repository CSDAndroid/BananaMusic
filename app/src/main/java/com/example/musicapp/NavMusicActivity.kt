package com.example.musicapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.musicapp.network.ApiResponse
import com.example.musicapp.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NavMusicActivity : Nav() {

    private var musiclist1 = ArrayList<Music>()
    private lateinit var iv_album_cover: ImageView
    private lateinit var tv_song_name: TextView
    private lateinit var iv_play : ImageView



    override fun getLayoutId(): Int {
        return R.layout.activity_nav_music
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initActivity() {

        iv_album_cover = findViewById<ImageView>(R.id.iv_album_cover)
        tv_song_name = findViewById<TextView>(R.id.tv_song_name)
        iv_play = findViewById<ImageView>(R.id.iv_play)
        val tv_song_name = findViewById<TextView>(R.id.tv_song_name)
        val rv_song = findViewById<RecyclerView>(R.id.rv_song_rv)
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
            val intent = Intent(this, MusicPlayerActivity::class.java)
            startActivity(intent)

        })

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
        rv_song.adapter = adapter
        val layoutmanager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv_song.layoutManager = layoutmanager

        val t = (1..4).random()

            if (t > 0 && t <= 4) {
                RetrofitInstance.api.getMusic(t).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(
                        call: Call<ApiResponse?>,
                        response: Response<ApiResponse?>
                    ) {
                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            apiResponse?.data?.forEach { song ->
                                Log.d(
                                    "SongData",
                                    "Song: ${song.song}, Singer: ${song.sing}, ID: ${song.id}  url: ${song.url} pic : ${song.pic}"
                                )
                                val pic = song.pic.replace("http", "https")
                                val music = Music(
                                    song.song,
                                    song.sing,
                                    pic,
                                    R.drawable.music,
                                    song.id,
                                    song.url
                                )
                                musiclist1.add(music)
                                get_his_music(music)
                            }
                        } else {
                            Log.e("MainActivity", "Error: ${response.errorBody()?.string()}")
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Log.e("SongData", "Error: ${t.message}")
                    }
                })
            }
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