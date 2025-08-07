package com.example.musicapp.all_fun

data class LyricLine(val time: Long, val text: String)

fun parseLyrics(lyricsText: String): List<LyricLine>{
    val lines = lyricsText.split("\n")
    val lyrics = mutableListOf<LyricLine>()
    val timeRegex = Regex("\\[(\\d{2}):(\\d{2}\\.\\d{2})\\]")
    lines.forEach {line ->
        val match = timeRegex.find(line)
        if (match != null){
            val minutes = match.groupValues[1].toInt()
            val seconds = match.groupValues[2].toDouble()
            val time = (minutes * 60 + seconds) * 1000 // 转换为毫秒
            val text = line.replace(timeRegex, "").trim()
            lyrics.add(LyricLine(time.toLong(), text))
        }
    }
    return lyrics
}
