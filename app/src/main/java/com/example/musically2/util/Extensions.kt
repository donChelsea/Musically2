package com.example.musically2.util

import com.example.musically2.domain.models.Track
import com.google.firebase.firestore.QueryDocumentSnapshot

fun QueryDocumentSnapshot.toTrack(
    index: Int,
    trackUrl: String,
    imageUrl: String,
) = Track(
    image = imageUrl,
    title = this.getString(TITLE).orEmpty(),
    artist = this.getString(ARTIST).orEmpty(),
    fileName = this.getString(FILENAME).orEmpty(),
    isPlaying = false,
    index = index,
    trackUrl = trackUrl
)