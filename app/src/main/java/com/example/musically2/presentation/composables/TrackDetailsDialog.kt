package com.example.musically2.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musically2.R
import com.example.musically2.domain.models.Track
import com.example.musically2.util.MOCK_TRACK

@Composable
fun TrackDetailsDialog(
    modifier: Modifier = Modifier,
    track: MutableState<Track?>,
    openDialog: MutableState<Boolean>,
) {
    if (openDialog.value) {
        val viewingTrack = track.value as Track

        AlertDialog(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            title = {
                Text(text = viewingTrack.title)
            },
            text = {
                Column {
                    Text(text = "${stringResource(id = R.string.track_number_label)} ${viewingTrack.index}")
                    Text(text = "${stringResource(id = R.string.artist_label)} ${viewingTrack.artist}")
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        onClick = { openDialog.value = false }
                    ) {
                        Text(text = stringResource(id = R.string.dismiss))
                    }
                }
            },
            onDismissRequest = { openDialog.value = false },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTrackDetailsDialog() {
    TrackDetailsDialog(
        track = remember { mutableStateOf(MOCK_TRACK) },
        openDialog = remember { mutableStateOf(true) }
    )
}