package com.example.musicapp


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.network.ApiResponse
import com.example.musicapp.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class AlbumListActivity : AppCompatActivity() {
    private lateinit var  iv_album_cover : ImageView
    private lateinit var  tv_album_title : TextView
    private lateinit var  tv_artist : TextView
    private lateinit var  tv_song_name : TextView

    private val musiclist1 = ArrayList<Music>()

    private lateinit var adapter: TitleAdapter
    var t = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_album_list)
        actionBar?.hide()



        t = getIntent().getIntExtra("t", 0) + 1
        Log.d("SongData", "onCreate: $t")


        val rv_songs = findViewById<RecyclerView>(R.id.rv_songs)
        iv_album_cover = findViewById<ImageView>(R.id.iv_album_cover)
        tv_album_title = findViewById<TextView>(R.id.tv_album_title)
        tv_artist = findViewById<TextView>(R.id.tv_artist)
        tv_song_name = findViewById<TextView>(R.id.tv_song_name)


        adapter = TitleAdapter(musiclist1,{ music ->
            // 点击事件回调
            loadAlbumCover(music.pic)
            tv_album_title.text = music.song
            tv_artist.text = music.sing
            getSharedPreferences("data",MODE_PRIVATE).edit{
                putString("song","${music.song}")
                putString("sing","${music.sing}")
                putString("pic_url","${music.pic}")
                putLong("music_id", music.id)
                putString("music_url","${music.url}")
            }
            val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
            val song = prefs.getString("song","").toString()
            val url = prefs.getString("music_url","")
            Log.e("data5", "onCreate: $url $song", )
        })




        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val song = prefs.getString("song","").toString()
        val sing = prefs.getString("sing","")
        val pic = prefs.getString("pic_url","").toString()
        val url = prefs.getString("music_url","")
        Log.e("data5", "onCreate: $url", )

        tv_album_title.text = song
        tv_artist.text = sing
        loadAlbumCover(pic)
        rv_songs.adapter = adapter
        rv_songs.layoutManager = LinearLayoutManager(this)
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


    private fun loadAlbumCover(picUrl: String) {
        Glide.with(this)
            .load(picUrl) // 网络图片 URL
            .placeholder(R.drawable.fm1) // 加载中的占位图
            .error(R.drawable.fm1) // 加载失败的错误图
            .into(iv_album_cover)
    }
}

