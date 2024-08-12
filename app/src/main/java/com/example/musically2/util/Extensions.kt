package com.example.musically2.util

import com.example.musically2.domain.models.Track
import com.google.firebase.firestore.QueryDocumentSnapshot

fun QueryDocumentSnapshot.toTrack(index: Int) = Track(
    imageUrl = this.getString(IMAGE_URL).orEmpty(),
    name = this.getString(NAME).orEmpty(),
    artist = this.getString(ARTIST).orEmpty(),
    fileName = this.getString(FILENAME).orEmpty(),
    isPlaying = false,
    index = index,
    trackUrl = this.getString(TRACK_URL).orEmpty()
)