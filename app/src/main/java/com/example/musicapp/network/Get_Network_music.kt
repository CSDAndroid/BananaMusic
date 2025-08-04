package com.example.musicapp.network

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.Music
import com.example.musicapp.R

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




interface MusicCallback {
    fun onSuccess(musicList: ArrayList<Music>)
    fun onFailure(error: String)
}

fun Get_Network_Music(t: Int, callback: MusicCallback) {
    RetrofitInstance.api.getMusic(t).enqueue(object : Callback<ApiResponse> {
        override fun onResponse(
            call: Call<ApiResponse?>,
            response: Response<ApiResponse?>
        ) {
            if (response.isSuccessful) {
                val apiResponse = response.body()
                val musicList = ArrayList<Music>()
                apiResponse?.data?.forEach { song ->
                    Log.d(
                        "SongData2",
                        "Song: ${song.song}, Singer: ${song.sing}, ID: ${song.id}  url: ${song.url} pic : ${song.pic}"
                    )
                    val pic = song.pic.replace("http", "https")
                    val music = Music(
                        song.song,
                        song.sing,
                        pic,
                        R.drawable.music,
                        song.id,
                        song.url
                    )
                    musicList.add(music)
                }
                callback.onSuccess(musicList)
            } else {
                Log.e("MainActivity", "Error: ${response.errorBody()?.string()}")
                callback.onFailure("Error: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            Log.e("SongData", "Error: ${t.message}")
            callback.onFailure("Error: ${t.message}")
        }
    })
}