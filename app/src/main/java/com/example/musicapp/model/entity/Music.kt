package com.example.musicapp.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "music") // 必须有 @Entity 注解，tableName 定义表名
data class Music(
    val song: String,
    val sing: String,
    val pic: String,
    val pic1: Int,
    @PrimaryKey val id: Long,
    val url: String
): Serializable
