package com.abhinav.traveljournal.common

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AudioPlayer(
    audioPath: String
){
    val mediaPlayer = remember { MediaPlayer() }

    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableIntStateOf(0) }
    var currentPosition by remember { mutableIntStateOf(0) }

    // Prepare media
    LaunchedEffect(audioPath) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(audioPath)
        mediaPlayer.prepare()
        duration = mediaPlayer.duration
        currentPosition = 0
        isPlaying = false
    }

    //Update progress while playing
    LaunchedEffect(isPlaying) {
        while (isPlaying){
            currentPosition = mediaPlayer.currentPosition
            delay(500)
        }
    }

    //Cleanup
    DisposableEffect(Unit) {
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
            currentPosition = 0
            mediaPlayer.seekTo(0)
        }
        onDispose {
            mediaPlayer.release()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //Play and Pause
        Button(
            onClick = {
                if (isPlaying){
                    mediaPlayer.pause()
                }else{
                    mediaPlayer.start()
                }
                isPlaying = !isPlaying
            }
        ) {
            Text(if(isPlaying) "Pause" else "Start")
        }

        //Progress slider
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = {
                currentPosition = it.toInt()
                mediaPlayer.seekTo(it.toInt())
            },
            valueRange = 0f..duration.toFloat()
        )

        //Time
        Text(
            text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
private fun formatTime(ms: Int):String{
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes,seconds)
}
