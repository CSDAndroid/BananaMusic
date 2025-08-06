package com.example.musicapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.network.Get_Network_Music
import com.example.musicapp.network.MusicCallback

private var musiclist1 = ArrayList<Music>()
private var musiclist2 = ArrayList<Music>()
private lateinit var historyAdapter: HistoryAdapter


class MainActivity : Nav() {
    private lateinit var iv_album_cover: ImageView

    private lateinit var tv_song_name: TextView
    private lateinit var mainViewModel: MainViewModel
    private lateinit var iv_play : ImageView
    private lateinit var actionbarViewModel: ActionbarViewModel
    // 适配器实例
    private lateinit var actionbarAdapter: ActionbarAdapter1
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
        return R.layout.activity_main
    }

    override fun initActivity() {
        supportActionBar?.hide()

        // 初始化Actionbar相关组件
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        actionbarViewModel = ViewModelProvider(this)[ActionbarViewModel::class.java]
        iv_album_cover = findViewById(R.id.iv_album_cover)
        tv_song_name = findViewById(R.id.tv_song_name)
        iv_play = findViewById<ImageView>(R.id.iv_play)


        registerPlaybackStateListener(playbackStateListener)





        // 从SharedPreferences中读取数据
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        val song = prefs.getString("song", "") ?: ""
        val sing = prefs.getString("sing", "1") ?: ""
        val pic = prefs.getString("pic_url", "") ?: ""
        val url = prefs.getString("music_url", "") ?: ""


        var yesOrNo = getYesOrNo()
        if (yesOrNo){
            iv_play.setImageResource(R.drawable.stop)
        }else{
            iv_play.setImageResource(R.drawable.ic_play)
        }

        iv_play.setOnClickListener {
            val url_new = prefs.getString("music_url", "") ?: ""
            if (yesOrNo){
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
        // 更新MusicBarManager中的状态
        MusicBarManager.updateMusicInfo(
            songName = song,
            singerName = prefs.getString("sing", "") ?: "",
            albumCover = pic,
            musicId = prefs.getLong("music_id", 0L),
            musicUrl = prefs.getString("music_url", "") ?: ""
        )

        // 设置歌曲名称
        tv_song_name.text = "$song - $sing"

        // 加载专辑封面
        loadAlbumCover(pic)

        // 初始化视图
        initViews()

        // 初始化适配器
        initAdapters()

        // 观察数据变化
        observeData()




        val currentState = getCurrentPlaybackState() // 获取当前播放状态
        playbackStateListener.onPlaybackStateChanged(currentState)
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

        // 获取网络音乐数据
        Get_Network_Music(1, object : MusicCallback {
            override fun onSuccess(musicList: ArrayList<Music>) {
                // 更新音乐列表
                musiclist1 = musicList

                // 初始化历史记录适配器
                historyAdapter = HistoryAdapter(musiclist1) { music ->
                    // 点击事件的处理逻辑
                    val intent = Intent(this@MainActivity, MusicPlayerActivity::class.java)
                    intent.putExtra("music_name", music.song)
                    intent.putExtra("music_singer", music.sing)
                    intent.putExtra("music_pic", music.pic)
                    intent.putExtra("music_id", music.id)
                    intent.putExtra("music_url", music.url)
                    startActivity(intent)

                    // 更新SharedPreferences
                    getSharedPreferences("data", MODE_PRIVATE).edit {
                        putString("song", "${music.song}")
                        putString("sing", "${music.sing}")
                        putString("pic_url", "${music.pic}")
                        putLong("music_id", music.id)
                        putString("music_url", "${music.url}")
                    }

                    // 更新MusicBarManager状态
                    MusicBarManager.updateMusicInfo(
                        songName = music.song,
                        singerName = music.sing,
                        albumCover = music.pic,
                        musicId = music.id,
                        musicUrl = music.url
                    )
                }

                // 设置适配器
                findViewById<RecyclerView>(R.id.history_list).adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()

                // 日志输出
                Log.d("data3", "initAdapters: $musiclist1")
            }

            override fun onFailure(error: String) {
                Log.e("error1", "onFailure: $error")
            }
        })

        // 导航栏点击事件
        actionbarAdapter.setOnItemClickListener(object : ActionbarAdapter1.OnItemClickListener {
            override fun onItemClick(position: Int) {
                actionbarAdapter.setSelectedPosition(position)
                Log.d("SongData", "onCreate: $position")
                val intent = Intent(this@MainActivity, AlbumListActivity::class.java)
                intent.putExtra("t", position)
                startActivity(intent)
            }
        })
        val t = (1..4).random()
        Get_Network_Music(t, object : MusicCallback {
            @SuppressLint("WrongViewCast")
            override fun onSuccess(musicList: ArrayList<Music>) {
                musiclist2 = musicList
                val adapter = TitleAdapterDay(musiclist2, { music ->
                    val intent = Intent(this@MainActivity, MusicPlayerActivity::class.java)
                    intent.putExtra("music_name", music.song)
                    intent.putExtra("music_singer", music.sing)
                    intent.putExtra("music_pic", music.pic)
                    intent.putExtra("music_id", music.id)
                    intent.putExtra("music_url", music.url)
                    startActivity(intent)

                    // 更新SharedPreferences
                    getSharedPreferences("data", MODE_PRIVATE).edit {
                        putString("song", "${music.song}")
                        putString("sing", "${music.sing}")
                        putString("pic_url", "${music.pic}")
                        putLong("music_id", music.id)
                        putString("music_url", "${music.url}")
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
                val rv_day = findViewById<RecyclerView>(R.id.rv_day)
                rv_day.adapter = adapter
                val layoutManager =
                    GridLayoutManager(this@MainActivity, 3, GridLayoutManager.HORIZONTAL, false)
                rv_day.layoutManager = layoutManager
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(error: String) {
                TODO("Not yet implemented")
            }

        })
    }

    ///进入页面时触发一次监听
    private fun getCurrentPlaybackState(): PlaybackState {
        return if (getYesOrNo()) { // 假设isMusicPlaying()是判断当前是否在播放的方法
            PlaybackState.PLAYING
        } else {
            PlaybackState.PAUSED
        }
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterPlaybackStateListener(playbackStateListener)
        getSharedPreferences("data", Context.MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }
}

