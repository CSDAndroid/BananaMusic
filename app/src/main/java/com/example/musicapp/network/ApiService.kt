package com.example.musicapp.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService{
    @GET("/api/music/wy/top")
    fun getMusic(@Query("t") t : Int ): Call<ApiResponse>
}