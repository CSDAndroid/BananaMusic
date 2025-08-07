package com.example.musicapp.all_fun

import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private var mediaPlayer: MediaPlayer? = null
private var playbackState = PlaybackState.IDLE
private val playbackTimeUpdateInterval = 1000L // 更新间隔为1秒
private var playbackTimeUpdateJob: Job? = null
private var retryCount = 0
private var Is_play = ""
private var YesOrNo = false
private val playbackStateListeners = mutableListOf<PlaybackStateListener>()

enum class PlaybackState { IDLE, PREPARING, PLAYING, PAUSED, ERROR }



interface PlaybackStateListener {
    fun onPlaybackStateChanged(state: PlaybackState)
    fun onPlaybackTimeChanged(currentTime: Int)
}
fun registerPlaybackStateListener(listener: PlaybackStateListener) {
    playbackStateListeners.add(listener)
}

fun unregisterPlaybackStateListener(listener: PlaybackStateListener) {
    playbackStateListeners.remove(listener)
}

private fun notifyPlaybackStateChange() {
    playbackStateListeners.forEach { listener ->
        listener.onPlaybackStateChanged(playbackState)
    }
}

fun playAudio(url: String) {
    if (playbackState == PlaybackState.PLAYING) {
        stopPlayback()
    }

    Log.d("data2", "准备播放: $url")
    playbackState = PlaybackState.PREPARING
    notifyPlaybackStateChange()
    Is_play = url
    YesOrNo = true

    CoroutineScope(Dispatchers.IO).launch {
        try {
            mediaPlayer?.let {
                it.reset()
            } ?: run {
                mediaPlayer = MediaPlayer().apply {
                    setOnCompletionListener {
                        Log.d("data2", "播放完成")
                        playbackState = PlaybackState.IDLE
                        notifyPlaybackStateChange()
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("data2", "播放错误: what=$what, extra=$extra")
                        playbackState = PlaybackState.ERROR
                        if (retryCount < 3) {
                            retryCount++
                            playAudio(url) // 自动重试
                        } else {
                            releasePlayer()

                        }
                        false
                    }
                    setOnPreparedListener { mp ->
                        mp.start()
                        playbackState = PlaybackState.PLAYING
                        Log.d("data2", "开始播放")
                        startPlaybackTimeUpdates()
                    }
                }
            }

            withContext(Dispatchers.IO) {
                mediaPlayer?.apply {
                    setDataSource(url)
                    prepareAsync() // 异步准备
                }
            }
        } catch (e: Exception) {
            Log.e("data2", "播放失败: ${e.message}", e)
            playbackState = PlaybackState.ERROR
            releasePlayer()
        }
    }
}
private fun startPlaybackTimeUpdates() {
    playbackTimeUpdateJob?.cancel()
    playbackTimeUpdateJob = CoroutineScope(Dispatchers.Main).launch {
        while (playbackState == PlaybackState.PLAYING) {
            val currentTime = mediaPlayer?.currentPosition ?: 0
            notifyPlaybackTimeChange(currentTime)
            delay(playbackTimeUpdateInterval)
        }
    }
}

private fun notifyPlaybackTimeChange(currentTime: Int) {
    playbackStateListeners.forEach { listener ->
        listener.onPlaybackTimeChanged(currentTime)
    }
}

fun stopPlayback() {
    mediaPlayer?.let {
        if (it.isPlaying) {
            it.stop()
        }
        it.reset()
        playbackState = PlaybackState.IDLE
        notifyPlaybackStateChange()
        Log.d("data2", "停止播放")
    }
}
fun stop_Or_start(url: String){
    Log.d("data2", "停止播放$Is_play")
    if (Is_play == url){
        if (playbackState == PlaybackState.PLAYING){
            mediaPlayer?.pause()
            playbackState = PlaybackState.PAUSED
            notifyPlaybackStateChange()
            Log.d("data2", "暂停播放$Is_play")
            YesOrNo = false

        } else{
            mediaPlayer?.start()
            playbackState = PlaybackState.PLAYING
            notifyPlaybackStateChange()
            Log.d("data2", "继续播放$Is_play")

            startPlaybackTimeUpdates()
        }
    }else{
        playAudio(url)
    }

}

fun releasePlayer() {
    mediaPlayer?.release()
    mediaPlayer = null
    playbackState = PlaybackState.IDLE
    notifyPlaybackStateChange()
    Log.d("data2", "释放播放器")
}
fun getYesOrNo() : Boolean{
    return YesOrNo
}