package com.example.musicapp.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "music",
    indices = [Index(value = ["id"], unique = true)] // 为 id 字段创建唯一索引
)
data class Music(
    val song: String,
    val sing: String,
    val pic: String,
    val pic1: Int,
    val id: Long,
    val url: String
): Serializable{
    @PrimaryKey(autoGenerate = true)
    var db_id: Long = 0
}
