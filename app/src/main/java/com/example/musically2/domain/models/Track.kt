package com.example.musically2.domain.models

data class Track(
    val image: String,
    val index: Int,
    val title: String,
    val artist: String,
    val trackUrl: String,
    var isPlaying: Boolean,
    val fileName: String
)