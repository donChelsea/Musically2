package com.example.musically2.util

import com.example.musically2.domain.models.Track

const val TITLE = "title"
const val ARTIST = "artist"
const val FILENAME = "fileName"
const val IMAGE = "albumArt"
const val ALBUM_ART = "AlbumArt"
const val TRACKS = "tracks"
val MOCK_TRACK = Track(
    image = "",
    index = 0,
    title = "Title",
    artist = "Artist",
    trackUrl = "",
    isPlaying = true,
    fileName = "fileName"
)