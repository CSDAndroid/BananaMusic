package com.example.musicapp.model.network

import com.example.musicapp.model.entity.ApiResponse
import com.example.musicapp.model.entity.MusicResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService{
    @GET("/api/music/wy/top")
    fun getMusic(@Query("t") t : Int ): Call<ApiResponse>
}

interface MusicService {
    @GET("int/v1/dg_netease")
    fun getMusicData(@Query("msg") msg: String, @Query("format") format: String, @Query("n") n: String): Call<MusicResponse>
}