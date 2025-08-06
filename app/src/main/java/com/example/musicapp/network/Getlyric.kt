package com.example.musicapp.network

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object Getlyric {
    private const val BASE_URL = "https://music.163.com/api/song/lyric?os=pc&id=%s&lv=-1&kv=-1&tv=-1"
    fun getlyric(song_id : String,callback : (String) -> Unit) {
        val url = String.format(BASE_URL, song_id)
        val request = Request.Builder().url(url).build()
        OkHttpClient().newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback("Failed to fetch lyrics: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful){
                    callback("Unexpected code $response")
                    return
                }
                val responseBody = response.body?.string() ?: "wu"
                val jsonResponse = JSONObject(responseBody)
                val lyric = jsonResponse.getJSONObject("lrc").getString("lyric")
                callback(lyric)
                // 检查是否有歌词数据
                if (jsonResponse.has("lrc") && !jsonResponse.isNull("lrc")) {
                    val lrc = jsonResponse.getJSONObject("lrc")
                    if (lrc.has("lyric") && !lrc.isNull("lyric")) {
                        val lyric = lrc.getString("lyric")
                        callback(lyric)
                    } else {
                        callback("No lyrics available for this song.")
                    }
                } else {
                    callback("No lyrics available for this song.")
                }
            }
        })
    }
}