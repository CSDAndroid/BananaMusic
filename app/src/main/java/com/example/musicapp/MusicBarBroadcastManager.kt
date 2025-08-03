package com.example.musicapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import android.animation.ObjectAnimator

/**
 * MusicBar广播管理器
 * 用于在Activity之间同步music_bar的显示/隐藏状态
 */
object MusicBarBroadcastManager {
    
    const val ACTION_MUSIC_BAR_STATE_CHANGED = "com.example.musicapp.MUSIC_BAR_STATE_CHANGED"
    const val EXTRA_IS_VISIBLE = "is_visible"
    const val EXTRA_TRANSLATION_Y = "translation_y"
    
    private var currentState = MusicBarState(true, 0f)
    
    data class MusicBarState(
        val isVisible: Boolean,
        val translationY: Float
    )
    
    /**
     * 广播接收器
     */
    class MusicBarStateReceiver(
        private val musicBarView: View,
        private val onStateChanged: (Boolean) -> Unit
    ) : BroadcastReceiver() {
        
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_MUSIC_BAR_STATE_CHANGED) {
                val isVisible = intent.getBooleanExtra(EXTRA_IS_VISIBLE, true)
                val translationY = intent.getFloatExtra(EXTRA_TRANSLATION_Y, 0f)
                
                // 更新当前状态
                currentState = MusicBarState(isVisible, translationY)
                
                // 立即更新UI，不使用动画
                musicBarView.translationY = translationY
                
                // 回调通知状态变化
                onStateChanged(isVisible)
            }
        }
    }
    
    /**
     * 注册广播接收器
     */
    fun registerReceiver(
        context: Context,
        musicBarView: View,
        onStateChanged: (Boolean) -> Unit
    ): MusicBarStateReceiver {
        val receiver = MusicBarStateReceiver(musicBarView, onStateChanged)
        val filter = IntentFilter(ACTION_MUSIC_BAR_STATE_CHANGED)
        context.registerReceiver(receiver, filter)
        
        // 只同步UI位置，不调用回调
        musicBarView.translationY = currentState.translationY
        
        return receiver
    }
    
    /**
     * 注销广播接收器
     */
    fun unregisterReceiver(context: Context, receiver: MusicBarStateReceiver) {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            // 忽略注销错误
        }
    }
    
    /**
     * 广播状态变化
     */
    fun broadcastStateChange(context: Context, isVisible: Boolean, translationY: Float) {
        currentState = MusicBarState(isVisible, translationY)
        
        val intent = Intent(ACTION_MUSIC_BAR_STATE_CHANGED).apply {
            putExtra(EXTRA_IS_VISIBLE, isVisible)
            putExtra(EXTRA_TRANSLATION_Y, translationY)
        }
        context.sendBroadcast(intent)
    }
    
    /**
     * 获取当前状态
     */
    fun getCurrentState(): MusicBarState = currentState
} 