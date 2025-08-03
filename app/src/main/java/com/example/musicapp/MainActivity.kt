package com.example.musicapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

private var musiclist1 = ArrayList<Music>()
private lateinit var historyAdapter: HistoryAdapter

class MainActivity : Nav() {
    private lateinit var iv_album_cover: ImageView
    private lateinit var tv_song_name: TextView
    private lateinit var mainViewModel: MainViewModel
    private lateinit var actionbarViewModel: ActionbarViewModel
    // 适配器实例
    private lateinit var actionbarAdapter: ActionbarAdapter1

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initActivity() {
        supportActionBar?.hide()
        
        // 初始化Actionbar相关组件
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        actionbarViewModel = ViewModelProvider(this)[ActionbarViewModel::class.java]
        iv_album_cover = findViewById(R.id.iv_album_cover)
        tv_song_name = findViewById(R.id.tv_song_name)

        // 从SharedPreferences中读取数据
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        val song = prefs.getString("song", "") ?: ""
        val pic = prefs.getString("pic_url", "") ?: ""

        // 更新MusicBarManager中的状态
        MusicBarManager.updateMusicInfo(
            songName = song,
            singerName = prefs.getString("sing", "") ?: "",
            albumCover = pic,
            musicId = prefs.getLong("music_id", 0L),
            musicUrl = prefs.getString("music_url", "") ?: ""
        )

        // 设置歌曲名称
        tv_song_name.text = song

        // 加载专辑封面
        loadAlbumCover(pic)
        
        // 初始化视图
        initViews()

        // 初始化适配器
        initAdapters()
        
        // 观察数据变化
        observeData()
    }

    // 初始化视图组件
    private fun initViews() {
        // 导航栏RecyclerView
        val actionBarRecyclerView: RecyclerView = findViewById(R.id.action_bar_recyclerview)
        actionBarRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 历史记录RecyclerView
        val historyRecyclerView: RecyclerView = findViewById(R.id.history_list)
        historyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    // 初始化适配器
    private fun initAdapters() {
        // 导航栏适配器
        actionbarAdapter = ActionbarAdapter1()
        findViewById<RecyclerView>(R.id.action_bar_recyclerview).adapter = actionbarAdapter

        // 历史记录适配器
        historyAdapter = HistoryAdapter(musiclist1,{ music ->
            // 点击事件的处理逻辑
            val intent = Intent(this@MainActivity, MusicPlayerActivity::class.java)
            intent.putExtra("music_name", music.song)
            intent.putExtra("music_singer", music.sing)
            intent.putExtra("music_pic", music.pic)
            intent.putExtra("music_id", music.id)
            intent.putExtra("music_url", music.url)
            startActivity(intent)
            
            // 更新SharedPreferences
            getSharedPreferences("data",MODE_PRIVATE).edit{
                putString("song","${music.song}")
                putString("sing","${music.sing}")
                putString("pic_url","${music.pic}")
                putLong("music_id", music.id)
                putString("music_url", music.url)
            }
            
            // 更新MusicBarManager状态
            MusicBarManager.updateMusicInfo(
                songName = music.song,
                singerName = music.sing,
                albumCover = music.pic,
                musicId = music.id,
                musicUrl = music.url
            )
        })
        historyAdapter.notifyDataSetChanged()
        findViewById<RecyclerView>(R.id.history_list).adapter = historyAdapter
        Log.d("data3", "initAdapters: $musiclist1")
        
        // 导航栏点击事件
        actionbarAdapter.setOnItemClickListener(object : ActionbarAdapter1.OnItemClickListener {
            override fun onItemClick(position: Int) {
                actionbarAdapter.setSelectedPosition(position)

                Log.d("SongData", "onCreate: $position")
                val intent = Intent(this@MainActivity, AlbumListActivity::class.java)
                intent.putExtra("t",position)
                startActivity(intent)
            }
        })
    }

    // 观察ViewModel数据变化
    private fun observeData() {
        // 观察导航栏选项数据
        actionbarViewModel.actionItems.observe(this) { items ->
            actionbarAdapter.submitList(items)
        }

        // 观察网络请求状态
        mainViewModel.requestStatus.observe(this) { isCompleted ->
            if (isCompleted) {
                // 可添加加载框隐藏逻辑
            }
        }

        // 观察导航栏选中位置（从ViewModel恢复状态）
        actionbarViewModel.selectedPosition.observe(this) { position ->
            if (position != -1) {
                actionbarAdapter.setSelectedPosition(position)
            }
        }
    }



    private fun loadAlbumCover(picUrl: String) {
        Glide.with(this)
            .load(picUrl) // 网络图片 URL
            .placeholder(R.drawable.fm1) // 加载中的占位图
            .error(R.drawable.fm1) // 加载失败的错误图
            .into(iv_album_cover)
    }
    
    private val sharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "song" || key == "pic_url" || key == "sing" || key == "music_id" || key == "music_url") {
                val song = sharedPreferences.getString("song", "") ?: ""
                val pic = sharedPreferences.getString("pic_url", "") ?: ""
                val sing = sharedPreferences.getString("sing", "") ?: ""
                val musicId = sharedPreferences.getLong("music_id", 0L)
                val musicUrl = sharedPreferences.getString("music_url", "") ?: ""

                // 更新UI
                tv_song_name.text = song
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

    override fun onDestroy() {
        super.onDestroy()
        getSharedPreferences("data", Context.MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }
}

fun get_his_music(music : Music){
    musiclist1.add(music)
    Log.d("data3", "get_his_music: $music")
    historyAdapter.notifyDataSetChanged()
}