package com.example.musically2.presentation.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musically2.OnMusicButtonClick
import com.example.musically2.R
import com.example.musically2.domain.models.MusicPlayerOption
import com.example.musically2.domain.models.Track

@Composable
fun Title() {
    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun Player(
    track: MutableState<Track>,
    isPlaying: MutableState<Boolean>,
    onMusicPlayerClick: OnMusicButtonClick,
    isTurntableArmFinished: MutableState<Boolean>,
    isBuffering: MutableState<Boolean>,
) {
    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = track.value.title,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Box(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            val canvasHeight = remember { mutableFloatStateOf(0f) }
            val musicBarAnim = rememberInfiniteTransition()
            val musicBarHeight by musicBarAnim.animateFloat(
                initialValue = 0f,
                targetValue = if (isTurntableArmFinished.value && isPlaying.value) {
                    canvasHeight.value
                } else 0f,
                animationSpec = infiniteRepeatable(
                    tween(500),
                    repeatMode = RepeatMode.Reverse
                ),
                label = ""
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(180F)
                    .background(Color.DarkGray.copy(0.4f))
            ) {
                val canvasWidth = this.size.width
                canvasHeight.value = this.size.height

                /* draw 8 rectangles along the canvas with a transparent color and a random height
                 an Offset, is configured to provide equal spacing between the bars
                 animation begins once turntable arm rotation is complete and will continue if music is playing
                 */
                for (i in 0..7) {
                    drawRect(
                        color = Color.DarkGray.copy(0.8f),
                        size = Size(canvasWidth / 9, musicBarHeight),
                        topLeft = Offset(x = canvasWidth / 8 * i.toFloat(), y = 0f)
                    )
                }
            }

            Row {
                Image(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous button",
                    modifier = Modifier
                        .clickable(
                            enabled = !isBuffering.value,
                            onClick = {
                                onMusicPlayerClick.onMusicButtonClick(MusicPlayerOption.PREVIOUS)
                            }
                        )
                        .padding(16.dp)
                        .size(35.dp)
                )
                Image(
                    imageVector = if (isPlaying.value) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    },
                    contentDescription = "Play Button",
                    modifier = Modifier
                        .clickable(
                            enabled = !isBuffering.value,
                            onClick = {
                                onMusicPlayerClick.onMusicButtonClick(MusicPlayerOption.PLAY)
                            }
                        )
                        .padding(16.dp)
                        .size(35.dp)
                )
                Image(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next button",
                    modifier = Modifier
                        .clickable(
                            enabled = !isBuffering.value,
                            onClick = {
                                onMusicPlayerClick.onMusicButtonClick(MusicPlayerOption.SKIP)
                            }
                        )
                        .padding(16.dp)
                        .size(35.dp)
                )
            }

            if (isBuffering.value) {
                ProgressOverlay()
            }
        }
    }
}

@Composable
fun TurnTable(
    isPlaying: MutableState<Boolean>,
    turntableArmState: MutableState<Boolean>,
    isTurntableArmFinished: MutableState<Boolean>,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        TurnTableDrawable(
            shape = RoundedCornerShape(20.dp),
            size = 240.dp,
            turntableDrawable = R.drawable.record,
            isPlaying = isPlaying,
            turntableArmState = turntableArmState,
            isTurntableArmFinished = isTurntableArmFinished
        )
    }
}


@Composable
fun TurnTableDrawable(
    shape: Shape,
    size: Dp,
    turntableDrawable: Int,
    isPlaying: MutableState<Boolean>,
    turntableArmState: MutableState<Boolean>,
    isTurntableArmFinished: MutableState<Boolean>,
) {
    val playingState = remember { isPlaying }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(0.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "")
            val turntableRotation by infiniteTransition.animateFloat(
                initialValue = 0F,
                targetValue = 360F,
                animationSpec = infiniteRepeatable(
                    animation = tween(5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = ""
            )

            val turntableArmRotation: Float by animateFloatAsState(
                targetValue = if (turntableArmState.value) 35f else 0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                finishedListener = {
                    isTurntableArmFinished.value = true
                }, label = ""
            )

            Image(
                painter = painterResource(id = turntableDrawable),
                contentDescription = "TurnTable",
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.Center)
                    .rotate(
                        degrees = if (playingState.value && isTurntableArmFinished.value) {
                            turntableRotation
                        } else {
                            0F
                        }
                    )
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.DarkGray)
                    .clip(CircleShape)
                    .padding(12.dp)
                    .align(Alignment.BottomStart)
            )

            Column(
                modifier = Modifier
                    .rotate(turntableArmRotation)
                    .align(Alignment.BottomStart)
                    .padding(12.dp, 0.dp, 0.dp, 0.dp)

            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp, height = 120.dp)
                        .background(Color.LightGray)
                        .border(BorderStroke(2.dp, Color.DarkGray))
                )
            }

        }
    }
}

@Composable
fun ProgressOverlay(
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .background(Color.Gray.copy(alpha = 0.6f))
                .fillMaxSize()
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
