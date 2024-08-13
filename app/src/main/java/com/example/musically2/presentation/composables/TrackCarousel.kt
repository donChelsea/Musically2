package com.example.musically2.presentation.composables

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musically2.R
import com.example.musically2.domain.models.Track

@Composable
fun TrackCarousel(
    modifier: Modifier = Modifier,
    isPlaying: MutableState<Boolean>,
    listState: LazyListState,
    playingSongIndex: MutableState<Int>,
    overlayIcon: ImageVector,
    tracks: List<Track>,
    onTrackItemClick: (Track) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        state = listState
    ) {
        items(items = tracks) { track ->
            TrackListItem(
                track = track,
                playingSongIndex,
                isPlaying,
                overlayIcon,
                onTrackItemClick
            )
        }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    playingSongIndex: MutableState<Int>,
    isPlaying: MutableState<Boolean>,
    overlayIcon: ImageVector,
    onTrackItemClick: (Track) -> Unit,
) {
    Row(
        modifier = Modifier.clickable(
            enabled = true,
            onClick = { onTrackItemClick(track) }
        )
    ) {
        Column {
            Box(modifier = Modifier.padding(6.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(track.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .size(120.dp)
                )
                if (playingSongIndex.value == track.index && isPlaying.value) {
                    OverlayRoundedBox(
                        shape = RoundedCornerShape(CornerSize(32.dp)),
                        color = Color.Gray.copy(alpha = 0.6f),
                        size = 120.dp,
                        overlayIcon = overlayIcon,
                        contentDescription = stringResource(R.string.play)
                    )
                }

            }
        }
    }
}

@Composable
fun OverlayRoundedBox(
    shape: Shape,
    color: Color,
    overlayIcon: ImageVector,
    size: Dp,
    showProgressBar: Boolean = false,
    contentDescription: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(color)
        ) {
            if (showProgressBar) {
                CircularProgressIndicator()
            } else {
                Image(
                    imageVector = overlayIcon,
                    contentDescription = contentDescription,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewTrackCarousel() {
//    TrackCarousel(
//        isPlaying =,
//        listState =,
//        playingSongIndex =,
//        overlayIcon =,
//        tracks =,
//        onTrackItemClick = {}
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewTrackListItem() {
//    TrackListItem(
//        track = ,
//        playingSongIndex = ,
//        isPlaying = ,
//        overlayIcon = ,
//        onTrackItemClick = ,
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewOverlayRoundedBox() {
//    OverlayRoundedBox(
//        shape = ,
//        color = ,
//        overlayIcon = ,
//        size = ,
//        showProgressBar = ,
//        contentDescription = ,
//    )
//}