package com.example.musically2.data

import com.example.musically2.domain.models.Track
import com.example.musically2.util.ALBUM_ART
import com.example.musically2.util.FILENAME
import com.example.musically2.util.IMAGE
import com.example.musically2.util.TRACKS
import com.example.musically2.util.toTrack
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TrackRepository {
    private val storage = Firebase.storage
    private val albumArtRef = storage.reference.child(ALBUM_ART)
    private val tracksRef = storage.reference

    val tracks = mutableListOf<Track>()

    suspend fun getTracks() = suspendCoroutine<List<Track>> { result ->
        Firebase.firestore.collection("traqs").get().addOnCompleteListener { task ->
            var index = 0

            task.result.forEach { document ->
                val imageUrl = albumArtRef.child(document.get(IMAGE).toString())
                val trackUrl = tracksRef.child(document.get(FILENAME).toString())

                imageUrl.downloadUrl.addOnSuccessListener { imageDownloadUrl ->
                    trackUrl.downloadUrl.addOnSuccessListener { trackDownloadUrl ->
                        tracks.add(
                            document.toTrack(
                                index = index,
                                imageUrl = imageDownloadUrl.toString(),
                                trackUrl = trackDownloadUrl.toString(),
                            )
                        )
                        if (index == task.result.size() - 1) {
                            result.resume(tracks)
                        }
                        index++
                    }
                }
            }
        }
    }
}