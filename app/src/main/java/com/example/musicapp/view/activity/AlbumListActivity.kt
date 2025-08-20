package com.example.musicapp.view.activity


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.musicapp.model.entity.Music
import com.example.musicapp.R
import com.example.musicapp.view.adapter.TitleAdapter
import com.example.musicapp.model.dataSource.remote.Get_Network_Music
import com.example.musicapp.model.dataSource.remote.MusicCallback


class AlbumListActivity : AppCompatActivity() {
    private lateinit var  iv_album_cover : ImageView
    private lateinit var  tv_album_title : TextView
    private lateinit var  tv_artist : TextView
    private lateinit var  tv_song_name : TextView

    private var musiclist1 = ArrayList<Music>()
    private lateinit var rv_songs : RecyclerView

    private lateinit var adapter: TitleAdapter
    var t = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_album_list)
        actionBar?.hide()
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        t = getIntent().getIntExtra("t", 0) + 1
        Log.d("SongData", "onCreate: $t")


        rv_songs = findViewById(R.id.rv_songs)
        iv_album_cover = findViewById(R.id.iv_album_cover)
        tv_album_title = findViewById(R.id.tv_album_title)
        tv_artist = findViewById(R.id.tv_artist)
        tv_song_name = findViewById(R.id.tv_song_name)


        val prefs = getSharedPreferences("data", MODE_PRIVATE)
        val song = prefs.getString("song","").toString()
        val sing = prefs.getString("sing","")
        val pic = prefs.getString("pic_url","").toString()
        val url = prefs.getString("music_url","")
        Log.e("data5", "onCreate: $url", )

        tv_album_title.text = song
        tv_artist.text = sing
        loadAlbumCover(pic)
        loadmusic(t)
        swipeRefreshLayout.setOnRefreshListener {
            loadmusic(t)
            swipeRefreshLayout.isRefreshing = false

        }



    }
    private fun loadmusic(t: Int){
        Get_Network_Music(t, object : MusicCallback {
            override fun onSuccess(musicList: ArrayList<Music>) {
                musiclist1 = musicList
                adapter = TitleAdapter(musiclist1, { music ->
                    // 点击事件回调
                    loadAlbumCover(music.pic)
                    tv_album_title.text = music.song
                    tv_artist.text = music.sing
                    getSharedPreferences("data", MODE_PRIVATE).edit {
                        putString("song", "${music.song}")
                        putString("sing", "${music.sing}")
                        putString("pic_url", "${music.pic}")
                        putLong("music_id", music.id)
                        putString("music_url", "${music.url}")
                    }
                    val prefs = getSharedPreferences("data", MODE_PRIVATE)
                    val song = prefs.getString("song", "").toString()
                    val url = prefs.getString("music_url", "")
                    Log.e("data5", "onCreate: $url $song",)
                })
                rv_songs.adapter = adapter
                rv_songs.layoutManager = LinearLayoutManager(this@AlbumListActivity)
            }

            override fun onFailure(error: String) {
                Log.d("error", "onFailure: $error")
            }

        })
    }


    private fun loadAlbumCover(picUrl: String) {
        Glide.with(this)
            .load(picUrl) // 网络图片 URL
            .placeholder(R.drawable.fm1) // 加载中的占位图
            .error(R.drawable.fm1) // 加载失败的错误图
            .into(iv_album_cover)
    }
}

