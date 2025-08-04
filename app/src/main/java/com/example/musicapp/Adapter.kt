package com.example.musicapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide

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
                onItemClickListener(music) // 回调点击事件
                playAudio(music.url)
            }
            music_item_play.setOnClickListener {
                val music = musicList[adapterPosition]
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
    private val musicList: ArrayList<Music>,
    private val onItemClickListener: (Music) -> Unit
) : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContentAdapter.ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.content_item,parent,false)
        return ViewHolder(view).apply {
            itemView.setOnClickListener {
                val music = musicList[adapterPosition]
                onItemClickListener(music) // 回调点击事件
                playAudio(music.url)
            }
        }
    }

    override fun onBindViewHolder(holder: ContentAdapter.ViewHolder, position: Int) {
        val music = musicList[position]
        holder.music_song.text = music.song
        holder.music_artist.text = music.sing
        Glide.with(holder.itemView.context)
            .load(music.pic) // 网络图片 URL
            .placeholder(R.drawable.music) // 加载中的占位图
            .error(R.drawable.music) // 加载失败的错误图
            .into(holder.card_image)
    }

    override fun getItemCount() = musicList.size
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val card_image = view.findViewById<ImageView>(R.id.card_image)
        val music_song = view.findViewById<TextView>(R.id.music_song)
        val music_artist = view.findViewById<TextView>(R.id.music_artist)
    }



}