package com.example.musicapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HistoryAdapter(private val musiclist : List<Music>,private val onItemClickListener: (Music) -> Unit) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.fengmian_item,parent,false)
        val holder = ViewHolder(view)
        holder.apply {
            itemView.setOnClickListener {
                val music = musiclist[adapterPosition]
                onItemClickListener(music) // 回调点击事件
                playAudio(music.url,music)
            }
        }

        return holder
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val music = musiclist[position]
        holder.musicname.text = music.song
        Glide.with(holder.itemView.context)
            .load(music.pic) // 网络图片 URL
            .placeholder(R.drawable.music) // 加载中的占位图
            .error(R.drawable.music) // 加载失败的错误图
            .into(holder.musicfengmian)
    }

    override fun getItemCount() = musiclist.size

    inner class ViewHolder(view : View): RecyclerView.ViewHolder(view){
        val musicfengmian = view.findViewById<ImageView>(R.id.musicfengmian)
        val musicname = view.findViewById<TextView>(R.id.musicname)
    }

}