package com.example.musicapp.network

import com.example.musicapp.Music

data class ApiResponse(val code : Int,val msg : String,val data : List<Music>)

data class MusicItem(
    val n: Int,
    val title: String,
    val singer: String,
    val pic: String
)

data class MusicResponse(
    val code: Int,
    val msg: String,
    val data: List<MusicItem>
)