package com.example.musicapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R

// 使用ListAdapter优化数据更新，泛型为数据类型String
class ActionbarAdapter1 : ListAdapter<String, ActionbarAdapter1.ViewHolder1>(ActionbarDiffCallback()) {

    // 临时时缓存选中位置（由外部通过ViewModel同步）
    private var currentSelectedPosition: Int = -1

    // 点击事件接口
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    // 外部调用更新选中位置
    fun setSelectedPosition(position: Int) {
        currentSelectedPosition = position
        notifyDataSetChanged() // 刷新选中状态
    }

    inner class ViewHolder1(view: View) : RecyclerView.ViewHolder(view) {
        val actionBarTitleName: TextView = view.findViewById(R.id.action_bar_title_name)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder1 {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.action_bar_item, parent, false)
        return ViewHolder1(view)
    }

    override fun onBindViewHolder(holder: ViewHolder1, position: Int) {
        val item = getItem(position)
        holder.actionBarTitleName.text = item

//        if (position == currentSelectedPosition) {
//            holder.actionBarTitleName.setTextColor(Color.GREEN)
//        } else {
//            holder.actionBarTitleName.setTextColor(Color.BLACK)
//        }
    }

    // DiffUtil优化数据更新效率
    private class ActionbarDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}