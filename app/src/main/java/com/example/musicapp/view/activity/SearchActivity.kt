package com.example.musicapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.view.adapter.TitleAdapter
import com.example.musicapp.model.entity.Music

import com.example.musicapp.model.dataSource.remote.Get_Search_Music
import com.example.musicapp.model.dataSource.remote.MusicCallback

private var musiclist1 = ArrayList<Music>()

class SearchActivity : AppCompatActivity() {
    private lateinit var iv_album_cover : ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val rv_hot_search = findViewById<RecyclerView>(R.id.rv_hot_search)
        val et_search = findViewById<EditText>(R.id.et_search)
        val btn_search = findViewById<Button>(R.id.btn_search)
        val tv_album_title = findViewById<TextView>(R.id.tv_album_title)
        iv_album_cover = findViewById<ImageView>(R.id.iv_album_cover)
        btn_search.setOnClickListener {
            val text = et_search.text.toString()
            Get_Search_Music(text,object : MusicCallback {
                @SuppressLint("WrongViewCast")
                override fun onSuccess(musicList: ArrayList<Music>) {
                    musiclist1 = musicList
                    val adapter = TitleAdapter(musiclist1, { music ->
                    })
                    rv_hot_search.adapter = adapter
                    rv_hot_search.layoutManager = LinearLayoutManager(this@SearchActivity)
                }

                override fun onFailure(error: String) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

}
