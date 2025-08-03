package com.example.musicapp

fun Testinit1(): ArrayList<Music>{
    val musiclist = ArrayList<Music>()
    for (i in 1..50) {
        if (i%3 == 0) {
            val music = Music("歌曲 $i", "歌手$i", "pic", R.drawable.item_1, 123, "url")
            musiclist.add(music)
        }else{
            val music = Music("歌曲 $i", "歌手$i", "pic", R.drawable.item_3, 123, "url")
            musiclist.add(music)
        }

    }
    return musiclist
}
