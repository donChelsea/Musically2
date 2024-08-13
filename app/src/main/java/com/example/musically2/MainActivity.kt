package com.example.musically2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.musically2.domain.models.MusicPlayerOption
import com.example.musically2.domain.models.Track
import com.example.musically2.presentation.TracksViewModel
import com.example.musically2.presentation.composables.Player
import com.example.musically2.presentation.composables.ShowLoading
import com.example.musically2.presentation.composables.Title
import com.example.musically2.presentation.composables.TrackCarousel
import com.example.musically2.presentation.composables.TrackDetailsDialog
import com.example.musically2.presentation.composables.TurnTable
import com.example.musically2.ui.theme.Musically2Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

import android.content.res.Configuration
import android.media.MediaPlayer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.CoroutineScope


@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnMusicButtonClick {

    private val isPlaying = mutableStateOf(false) // is music current being played
    private var trackList = listOf<Track>() // retrieve song list
    private lateinit var currentSong: MutableState<Track>// currently playing song
    private val clickedSong: MutableState<Track?> = mutableStateOf(null)// currently playing song
    private val currentSongIndex = mutableStateOf(-1) // used for recyclerview playing overlay
    private val turntableArmState = mutableStateOf(false)// turns turntable arm
    private val isBuffering = mutableStateOf(false)
    private val isTurntableArmFinished =
        mutableStateOf(false) // lets us know the turntable Arm rotation is finished
    private lateinit var listState: LazyListState // current state of album list
    private lateinit var coroutineScope: CoroutineScope // scope to be used in composables
    private lateinit var mediaPlayer: MediaPlayer
    private val tracksViewModel: TracksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
        setContent {
            Musically2Theme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    listState = rememberLazyListState()
                    coroutineScope = rememberCoroutineScope()
                    val openDialog = remember { mutableStateOf(false) }
                    val trackList by tracksViewModel.trackList.observeAsState()
                    if (trackList?.isNotEmpty() == true) {
                        MainContent(
                            isPlaying = isPlaying,
                            track = currentSong,
                            listState = listState,
                            onMusicPlayerClick = this@MainActivity,
                            currentSongIndex = currentSongIndex,
                            turntableArmState = turntableArmState,
                            isTurntableArmFinished = isTurntableArmFinished,
                            isBuffering = isBuffering,
                            tracks = trackList!!
                        ) { song ->
                            clickedSong.value = song
                            openDialog.value = true
                        }
                        TrackDetailsDialog(
                            track = clickedSong,
                            openDialog = openDialog
                        )
                    } else {
                        ShowLoading()
                    }
                }
            }
        }
    }


    private fun observeViewModel() {
        tracksViewModel.trackList.observeForever { list ->
            if (list.isNotEmpty()) {
                trackList = list
                currentSong = mutableStateOf(list.first())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> {
                window.statusBarColor = resources.getColor(R.color.white, null)
            }
            else -> {
                window.statusBarColor = resources.getColor(R.color.black, null)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized && isPlaying.value) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    private fun play() {
        try {
            if (this::mediaPlayer.isInitialized && isPlaying.value) {
                mediaPlayer.stop()
                mediaPlayer.release()
                isPlaying.value = false
                turntableArmState.value = false
                isTurntableArmFinished.value = false
            }
            isBuffering.value = true
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(currentSong.value.trackUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                isBuffering.value = false
                isPlaying.value = true
                if (!turntableArmState.value) {
                    turntableArmState.value = true
                }
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateList() {
        coroutineScope.launch {
            if (isPlaying.value) {
                currentSong.value.isPlaying = true
            }
            listState.animateScrollToItem(
                currentSong.value.index
            )
        }
    }

    override fun onMusicButtonClick(command: MusicPlayerOption) {
        when (command) {
            MusicPlayerOption.SKIP -> {
                // check list
                var nextSongIndex = currentSong.value.index + 1 // increment next
                // if current song is last song in the tracklist (track list starts at 0)
                if (currentSong.value.index == trackList.size - 1) {
                    nextSongIndex = 0 // go back to first song
                    if (isPlaying.value) {
                        currentSongIndex.value = 0 // playing song is first song in list
                    }
                } else {
                    currentSongIndex.value++ // increment song index
                }
                currentSong.value = trackList[nextSongIndex]

                if (isPlaying.value) {
                    play()
                }

                updateList()
            }
            MusicPlayerOption.PREVIOUS -> {
                var previousSongIndex = currentSong.value.index - 1 // increment previous
                // if current song is first song in the tracklist (track list starts at 0)
                if (currentSong.value.index == 0) {
                    previousSongIndex = trackList.lastIndex // go to last song in list
                    if (isPlaying.value) {
                        currentSongIndex.value =
                            trackList.lastIndex // last song is now playing song
                    }
                } else {
                    currentSongIndex.value-- // decrement current song
                }
                currentSong.value = trackList[previousSongIndex]

                if (isPlaying.value) {
                    play()
                }

                updateList()
            }

            MusicPlayerOption.PLAY -> {
                currentSong.value.isPlaying =
                    !isPlaying.value // confirms whether current song is played or paused
                currentSongIndex.value = currentSong.value.index //confirms current song Index
                try {
                    if (this::mediaPlayer.isInitialized && isPlaying.value) {
                        mediaPlayer.stop()
                        mediaPlayer.release()
                        isPlaying.value = false
                    } else play()
                } catch (e: Exception) {
                    mediaPlayer.release()
                    isPlaying.value = false
                }
            }
        }
    }
}


@Composable
fun MainContent(
    isPlaying: MutableState<Boolean>,
    track: MutableState<Track>,
    listState: LazyListState,
    onMusicPlayerClick: OnMusicButtonClick,
    currentSongIndex: MutableState<Int>,
    turntableArmState: MutableState<Boolean>,
    isTurntableArmFinished: MutableState<Boolean>,
    isBuffering: MutableState<Boolean>,
    tracks: List<Track>,
    onTrackItemClick: (Track) -> Unit,
) {
    Column {
        Title()
        TrackCarousel(
            isPlaying = isPlaying,
            listState = listState,
            playingSongIndex = currentSongIndex,
            overlayIcon = Icons.Default.Pause,
            tracks = tracks,
            onTrackItemClick = onTrackItemClick
        )
        TurnTable(
            isPlaying,
            turntableArmState,
            isTurntableArmFinished
        )
        Player(
            track = track,
            isPlaying = isPlaying,
            onMusicPlayerClick = onMusicPlayerClick,
            isTurntableArmFinished = isTurntableArmFinished,
            isBuffering = isBuffering
        )
    }
}

interface OnMusicButtonClick {
    fun onMusicButtonClick(command: MusicPlayerOption)
}