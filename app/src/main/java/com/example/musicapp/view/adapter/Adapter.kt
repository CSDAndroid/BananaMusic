package com.example.musicapp.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.model.entity.Music
import com.example.musicapp.R
import com.example.musicapp.model.entity.LyricLine
import com.example.musicapp.commonUtils.playAudio
import com.example.musicapp.commonUtils.stop_Or_start

class TitleAdapter(
    private val musicList: ArrayList<Music>,
    private val onItemClickListener: (Music) -> Unit
) : RecyclerView.Adapter<TitleAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val music_item_song: TextView = view.findViewById(R.id.music_item_song)
        val music_item_sing: TextView = view.findViewById(R.id.music_item_sing)
        val music_item_pic: ImageView = view.findViewById(R.id.music_item_pic)
        val music_item_play: ImageView = view.findViewById(R.id.music_item_play)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.title_recyclerview_item, parent, false)
        return ViewHolder(view).apply {
            itemView.setOnClickListener {
                val music = musicList[adapterPosition]
                onItemClickListener(music)
                stop_Or_start(music.url)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = musicList[position]
        holder.music_item_song.text = music.song
        holder.music_item_sing.text = music.sing
        Glide.with(holder.itemView.context)
            .load(music.pic) // 网络图片 URL
            .placeholder(R.drawable.music) // 加载中的占位图
            .error(R.drawable.music) // 加载失败的错误图
            .into(holder.music_item_pic)
    }

    override fun getItemCount() = musicList.size
}


class ContentAdapter(
    private val onItemClickListener: (Music) -> Unit
) : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

    private var _musicList: List<Music> = emptyList()
    fun submitList(newList: List<Music>) {
        _musicList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.card_image)
        val musicSong: TextView = view.findViewById(R.id.music_song)
        val musicArtist: TextView = view.findViewById(R.id.music_artist)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val music = _musicList[position]
                    onItemClickListener(music)
                    stop_Or_start(music.url)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.content_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = _musicList[position]

        holder.musicSong.text = music.song
        holder.musicArtist.text = music.sing

        Glide.with(holder.itemView.context)
            .load(music.pic)
            .placeholder(R.drawable.music)
            .error(R.drawable.music)
            .into(holder.cardImage)
    }

    override fun getItemCount(): Int = _musicList.size
}




class TitleAdapterDay(
    private val musicList: ArrayList<Music>,
    private val onItemClickListener: (Music) -> Unit
) : RecyclerView.Adapter<TitleAdapterDay.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val music_item_song: TextView = view.findViewById(R.id.music_item_song)
        val music_item_sing: TextView = view.findViewById(R.id.music_item_sing)
        val music_item_pic: ImageView = view.findViewById(R.id.music_item_pic)
        val music_item_play: ImageView = view.findViewById(R.id.music_item_play)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_day_item, parent, false)
        return ViewHolder(view).apply {
            itemView.setOnClickListener {
                val music = musicList[adapterPosition]
                onItemClickListener(music) // 回调点击事件
                stop_Or_start(music.url)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = musicList[position]
        holder.music_item_song.text = music.song
        holder.music_item_sing.text = music.sing
        Glide.with(holder.itemView.context)
            .load(music.pic) // 网络图片 URL
            .placeholder(R.drawable.music) // 加载中的占位图
            .error(R.drawable.music) // 加载失败的错误图
            .into(holder.music_item_pic)
    }

    override fun getItemCount() = musicList.size
}

class LyricAdapter(private val lyrics: List<LyricLine>) : RecyclerView.Adapter<LyricAdapter.LyricViewHolder>(){
    private var currentLineIndex = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LyricViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lyric_item,parent,false)
        val lyricViewHolder = LyricViewHolder(view)
        return lyricViewHolder
    }

    override fun onBindViewHolder(
        holder: LyricViewHolder,
        position: Int
    ) {
        holder.tvLyric.text = lyrics[position].text
        if (position == currentLineIndex){
            holder.tvLyric.setTextColor(Color.GREEN)
        }else{
            holder.tvLyric.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount(): Int = lyrics.size
    inner class LyricViewHolder(itemview : View): RecyclerView.ViewHolder(itemview){
        val tvLyric: TextView = itemView.findViewById(R.id.tvLyric)
    }
    fun setCurrentLineIndex(index: Int) {
        currentLineIndex = index
        notifyDataSetChanged()
    }
}