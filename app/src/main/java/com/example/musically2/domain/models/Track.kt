package com.example.musically2.domain.models

data class Track(
    val imageUrl: String,
    val index: Int,
    val name: String,
    val artist: String,
    val trackUrl: String,
    val isPlaying: Boolean,
    val fileName: String
)