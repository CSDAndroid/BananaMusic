package com.example.musicapp

import android.os.Bundle

class NavMusicActivity : Nav() {
    override fun getLayoutId(): Int {
        return R.layout.activity_nav_music
    }

    override fun initActivity() {
        // 在这里初始化NavMusicActivity特有的功能
        // 例如：设置标题、初始化视图等
    }
}