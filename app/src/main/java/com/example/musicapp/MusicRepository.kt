package com.example.musicapp.repository

import com.example.musicapp.Music
import com.example.musicapp.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import com.example.musicapp.R

class MusicRepository {

    suspend fun getMusic(page: Int): List<Music> {
        return withContext(Dispatchers.IO) { // 切换到IO线程执行网络请求
            try {
                // 执行网络请求（同步调用，已在IO线程）
                val response = RetrofitInstance.api.getMusic(page).execute()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    // 校验响应数据并转换格式
                    if (apiResponse?.code == 200 && apiResponse.data.isNotEmpty()) {
                        apiResponse.data.map { musicFromApi ->
                            // 假设接口返回的Music字段与本地Music类一致，直接转换并补充本地资源
                            musicFromApi.copy(pic1 = R.drawable.music)
                        }
                    } else {
                        Log.e("MusicRepository", "请求成功但数据异常，code: ${apiResponse?.code}")
                        emptyList()
                    }
                } else {
                    Log.e("MusicRepository", "请求失败，code: ${response.code()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("MusicRepository", "网络请求异常: ${e.message}", e)
                emptyList()
            }
        }
    }
}