package com.example.musicapp.view.activity

import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.viewmodel.ActionbarViewModel

import com.example.musicapp.R
import com.example.musicapp.view.adapter.ActionbarAdapter1
import com.example.musicapp.view.adapter.HistoryAdapter
import com.example.musicapp.view.adapter.TitleAdapterDay
import com.example.musicapp.commonUtils.PlaybackState
import com.example.musicapp.commonUtils.PlaybackStateManager
import com.example.musicapp.commonUtils.stop_Or_start
import com.example.musicapp.model.entity.Music
import com.example.musicapp.view.navigation.Nav
import com.example.musicapp.viewmodel.MainViewModel
import java.io.Serializable

class MainActivity : Nav() {
    private lateinit var iv_album_cover: ImageView
    private lateinit var tv_song_name: TextView
    private lateinit var iv_play: ImageView
    private lateinit var actionbarViewModel: ActionbarViewModel
    private lateinit var actionbarAdapter: ActionbarAdapter1
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var mainViewModel: MainViewModel

    // 适配器数据
    private var musiclist1 = ArrayList<Music>()
    private var musiclist2 = ArrayList<Music>()

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initActivity() {
        supportActionBar?.hide()
        // 初始化ViewModel
        initViewModels()
        // 初始化视图
        initViews()
        // 初始化适配器
        initAdapters()
        // 观察数据变化
        observeData()
        // 加载数据
        loadData()
        setupPlayButtonClick()
    }

    private fun initViewModels() {
        actionbarViewModel = ViewModelProvider(this)[ActionbarViewModel::class.java]
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun initViews() {
        iv_album_cover = findViewById(R.id.iv_album_cover)
        tv_song_name = findViewById(R.id.tv_song_name)
        iv_play = findViewById(R.id.iv_play)

        // 导航栏RecyclerView
        val actionBarRecyclerView: RecyclerView = findViewById(R.id.action_bar_recyclerview)
        actionBarRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 历史记录RecyclerView
        val historyRecyclerView: RecyclerView = findViewById(R.id.history_list)
        historyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initAdapters() {
        actionbarAdapter = ActionbarAdapter1()
        findViewById<RecyclerView>(R.id.action_bar_recyclerview).adapter = actionbarAdapter

        actionbarAdapter.setOnItemClickListener(object : ActionbarAdapter1.OnItemClickListener {
            override fun onItemClick(position: Int) {
                actionbarAdapter.setSelectedPosition(position)
                val intent = Intent(this@MainActivity, AlbumListActivity::class.java)
                intent.putExtra("t", position)
                startActivity(intent)
            }
        })
    }

    private fun loadData() {
        // 加载音乐列表
        mainViewModel.loadMusicList1()
        mainViewModel.loadRandomMusicList2()
    }

    private fun observeData() {
        // 观察导航栏数据
        actionbarViewModel.actionItems.observe(this) { items ->
            actionbarAdapter.submitList(items)
        }

        // 观察导航栏选中位置
        actionbarViewModel.selectedPosition.observe(this) { position ->
            if (position != -1) {
                actionbarAdapter.setSelectedPosition(position)
            }
        }

        // 观察音乐列表1（历史记录）
        mainViewModel.musicList1.observe(this) { musicList ->
            musiclist1 = musicList
            historyAdapter = HistoryAdapter(musiclist1) { music ->
                // 点击事件委托给ViewModel处理
                mainViewModel.onMusicItemClick(music)
                // 跳转播放页
                val intent = Intent(this@MainActivity, MusicPlayerActivity::class.java).apply {
                    putExtra("music_name", music.song)
                    putExtra("music_singer", music.sing)
                    putExtra("music_pic", music.pic)
                    putExtra("music_id", music.id)
                    putExtra("music_url", music.url)
                }
                intent.putExtra("MUSIC_LIST", musicList as Serializable)
                startActivity(intent)
            }
            findViewById<RecyclerView>(R.id.history_list).adapter = historyAdapter
            historyAdapter.notifyDataSetChanged()
        }

        // 观察音乐列表2（每日推荐）
        mainViewModel.musicList2.observe(this) { musicList ->
            musiclist2 = musicList
            val adapter = TitleAdapterDay(musiclist2) { music ->
                // 点击事件委托给ViewModel处理
                mainViewModel.onMusicItemClick(music)
                // 跳转播放页
                val intent = Intent(this@MainActivity, MusicPlayerActivity::class.java)
                intent.putExtra("music_name", music.song)
                intent.putExtra("music_singer", music.sing)
                intent.putExtra("music_pic", music.pic)
                intent.putExtra("music_id", music.id)
                intent.putExtra("music_url", music.url)
                startActivity(intent)
            }
            val rvDay = findViewById<RecyclerView>(R.id.rv_day)
            rvDay.adapter = adapter
            rvDay.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
            adapter.notifyDataSetChanged()
        }

        // 观察当前音乐信息
        mainViewModel.currentMusicInfo.observe(this) { (song, sing) ->
            tv_song_name.text = "$song - $sing"
        }

        // 观察专辑封面URL
        mainViewModel.albumCoverUrl.observe(this) { picUrl ->
            loadAlbumCover(picUrl)
        }

        PlaybackStateManager.playbackState.observe(this) { state ->
            updatePlayButtonState(state)
        }
    }

    // 播放按钮状态更新
    private fun updatePlayButtonState(state: PlaybackState) {
        val resourceId = when (state) {
            PlaybackState.IDLE, PlaybackState.PAUSED, PlaybackState.ERROR -> R.drawable.ic_play
            PlaybackState.PREPARING, PlaybackState.PLAYING -> R.drawable.stop
        }
        iv_play.setImageResource(0) // 清空旧资源
        iv_play.setImageResource(resourceId)
    }

    // 播放/暂停点击事件
    private fun setupPlayButtonClick() {
        iv_play.setOnClickListener {
            // 委托给ViewModel处理状态切换
            mainViewModel.togglePlayPause()
            // 执行播放/暂停操作
            val url = mainViewModel.getCurrentMusicUrl()
            stop_Or_start(url)
        }
    }

    private fun loadAlbumCover(picUrl: String) {
        Glide.with(this)
            .load(picUrl)
            .placeholder(R.drawable.music)
            .error(R.drawable.music)
            .into(iv_album_cover)
    }
}