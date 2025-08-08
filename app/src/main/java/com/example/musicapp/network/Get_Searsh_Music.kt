package com.example.musicapp.network

import android.util.Log
import com.example.musicapp.Music
import com.example.musicapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



fun Get_Search_Music(song: String,callback: MusicCallback){

    RetrofitClient.instance.getMusicData(song, "json", "").enqueue(object : Callback<MusicResponse> {
        override fun onResponse(call: Call<MusicResponse>, response: Response<MusicResponse>) {
            if (response.isSuccessful) {
                val musicResponse = response.body()
                val musicList = ArrayList<Music>()
                val result = StringBuilder()
                musicResponse?.data?.forEach { item ->
                    result.append("Number: ${item.n}, Title: ${item.title}, Singer: ${item.singer}, Pic: ${item.pic}\n")
                    val music = Music(
                        item.title,
                        item.singer,
                        item.pic,
                        R.drawable.music,
                        0,
                        ""
                    )
                    Log.e("data1", "Song: ${music.song}, Singer: ${music.sing}, ID: ${music.id}  url: ${music.url} pic : ${music.pic}", )
                    musicList.add(music)
                }
                callback.onSuccess(musicList)

            } else {
                Log.e("data1", "Error: ${response.errorBody()?.string()}" )
            }
        }

        override fun onFailure(call: Call<MusicResponse>, t: Throwable) {
            Log.e("data1",  "Error: ${t.message}")
            callback.onFailure("Error: ${t.message}")
        }
    })
}