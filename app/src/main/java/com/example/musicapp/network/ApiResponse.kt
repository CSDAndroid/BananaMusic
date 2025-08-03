package com.example.musicapp.network

import com.example.musicapp.Music

data class ApiResponse(val code : Int,val msg : String,val data : List<Music>)
