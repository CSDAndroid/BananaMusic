package com.example.musicapp.model.network

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

public object RetrofitInstance {
    private const val BASE_URL = "https://api.52vmy.cn"
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService ::class.java)
    }
}
object RetrofitClient {
    private const val BASE_URL = "https://api.ulq.cc/"

    val instance: MusicService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicService::class.java)
    }
}