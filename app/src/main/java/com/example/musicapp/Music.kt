package com.example.musicapp

import java.io.Serializable

data class Music(
    val song: String,
    val sing: String,
    val pic: String,
    val pic1: Int,
    val id: Long,
    val url: String
): Serializable
