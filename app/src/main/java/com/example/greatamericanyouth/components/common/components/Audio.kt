package com.example.greatamericanyouth.components.common.components

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Handler
import android.view.RoundedCorner
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.greatamericanyouth.R
import java.io.File
import kotlin.math.roundToInt
import kotlin.math.roundToLong

val ICON_SIZE = 40.dp

@Composable
fun AudioPlayer(fileName: String, modifier: Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var firstPlay by remember { mutableStateOf(true) }
    val player = remember { MediaPlayer() }
    var duration by remember { mutableIntStateOf(0) }
    var seconds by remember { mutableFloatStateOf(0.0f) }
    var progress by remember { mutableFloatStateOf(0.0f) }
    val handler = Handler()
    var isFinished by remember { mutableStateOf(false) }
    val sliderFps = 30.0f

    fun prepareAndStartPlaying(player: MediaPlayer, fileName: String) {
        player.setDataSource(fileName)
        player.prepare()
        player.start()
    }

    fun startPlaying(player: MediaPlayer) {
        player.start()
    }

    fun seekTo(player: MediaPlayer, ms: Int) {
        player.seekTo(ms)
        player.start()
    }

    fun restart(player: MediaPlayer) {
        seekTo(player, 0)
    }

    fun pause(player: MediaPlayer) {
        player.pause()
    }

    val onButtonClick = {
        isPlaying = !isPlaying
        when {
            // When the play button is first clicked, prepare the audio, start the timer
            isPlaying && firstPlay -> {
                prepareAndStartPlaying(player, fileName)
                duration = player.duration
                updateDuration(handler, (1000 / sliderFps).roundToLong()) {
                    if (isPlaying && !isFinished) {
                        seconds += (1.0f / sliderFps)
                        progress = (seconds * 1000) / duration.toFloat()
                    }
                }
                firstPlay = false
            }
            // On any subsequent plays just start playing
            isPlaying && !isFinished -> {
                startPlaying(player)
            }

            // When it is finished restart the player
            isPlaying -> {
                restart(player)
                isFinished = false
            }

            // Pause
            !isPlaying -> {
                pause(player)
            }
        }
    }
    player.setOnCompletionListener {
        isFinished = true
        isPlaying = false
        progress = 0.0f
        seconds = 0.0f
    }
    Row(modifier = modifier) {
        // start / stop
        IconButton(onClick = onButtonClick) {
            val playerIcon = if (isPlaying && !isFinished) Icons.Default.Pause else Icons.Default.PlayArrow
            Icon(
                imageVector = playerIcon, contentDescription = null,
                modifier = Modifier.size(ICON_SIZE).clip(RoundedCornerShape(16.dp))
            )
        }
        // pause / resume
        PlaybackDuration(seconds.roundToInt(), modifier = Modifier.padding(top = 12.dp, start = 4.dp))
        Spacer(modifier = Modifier.padding(8.dp))
        Slider(
            value = progress,
            onValueChange = {
                progress = it
                pause(player)
                isPlaying = false
            },
            onValueChangeFinished = {
                val seekToMs = (progress * duration).roundToInt()
                seekTo(player, seekToMs)
                seconds = seekToMs / 1000.0f
                startPlaying(player)
                isPlaying = true
            },
            modifier = Modifier.padding(end = 16.dp)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }
}

@Composable
private fun PlaybackDuration(seconds: Int, modifier: Modifier) {
    Row(modifier = modifier) {
        TextWrapper("${getFormattedMinutes(seconds)}:${getFormattedSeconds(seconds)}", 16)
    }
}

private fun getFormattedSeconds(seconds: Int) = String.format("%02d", seconds % 60)
private fun getFormattedMinutes(seconds: Int) = String.format("%02d", seconds / 60)

private fun updateDuration(handler: Handler, intervalTickMs: Long, onUpdate: () -> Unit) {
    handler.postDelayed({
        onUpdate()
        updateDuration(handler, intervalTickMs, onUpdate)
    }, intervalTickMs)
}

@Composable
fun AudioRecorder(context: Context, onSend: (Uri) -> Unit, onDelete: () -> Unit, modifier: Modifier) {
    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isInitialized by remember { mutableStateOf(false) }
    val fileName = "${context.filesDir}/recording.wav"
    val outputFile = remember { mutableStateOf<File?>(null) }
    var recorder = remember { MediaRecorder() }
    var seconds by remember { mutableIntStateOf(0) }
    val handler = Handler()
    var showPlayer by remember { mutableStateOf(false) }

    fun startRecording(recorder: MediaRecorder) {
        recorder.start()
    }

    fun prepareAndStartRecording(fileName: String, recorder: MediaRecorder): File {
        val file = File(fileName)
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(file.absolutePath)
        recorder.prepare()
        recorder.start()
        isInitialized = true
        return file
    }
    fun pauseRecording(recorder: MediaRecorder) {
        recorder.pause()
    }
    fun resumeRecording(recorder: MediaRecorder) {
        recorder.resume()
    }
    fun stopRecording(recorder: MediaRecorder) {
        recorder.stop()
    }

    val rowModifier = modifier.border(2.dp, Color.White, RoundedCornerShape(16.dp))
    val iconModifier = Modifier.size(ICON_SIZE).clip(RoundedCornerShape(16.dp))

    // start / stop
    if(showPlayer) {
        Row(modifier = rowModifier) {
            AudioPlayer(fileName, modifier)
            IconButton(onClick = {
                showPlayer = false
            }) {
                val refreshIcon = Icons.Default.Refresh
                Icon(
                    imageVector = refreshIcon,
                    contentDescription = null,
                    modifier = iconModifier
                )
            }
            IconButton(onClick = {
                onSend(Uri.fromFile(outputFile.value))
            }) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = colorResource(R.color.blue),
                    modifier = iconModifier
                )
            }
        }
    }
    else {
        Row(modifier = rowModifier) {
            IconButton(onClick = {
                isRecording = !isRecording
                if (isRecording && !isInitialized) {
                    outputFile.value = prepareAndStartRecording(fileName, recorder)
                    updateDuration(handler, 1000) {
                        if (isRecording && !isPaused) {
                            seconds++
                        }
                    }
                } else if (isRecording) {
                    startRecording(recorder)
                } else {
                    stopRecording(recorder)
                    seconds = 0
                    showPlayer = true
                }
            }) {
                val recorderIcon = if(isRecording) Icons.Default.Stop else Icons.Default.PlayArrow
                Icon(
                    imageVector = recorderIcon,
                    contentDescription = null,
                    modifier = iconModifier
                )
            }
            // pause / resume
            if(isRecording) {
                IconButton(onClick = {
                    isPaused = !isPaused
                    if(isPaused) {
                        resumeRecording(recorder)
                    }
                    else {
                        pauseRecording(recorder)
                    }
                }) {
                    val imageVector = if(isPaused) Icons.Default.PlayArrow else Icons.Default.Pause
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                        modifier = iconModifier
                    )
                }
                PlaybackDuration(seconds, modifier = Modifier.padding(top = 12.dp, start = 4.dp))
            }
        }
        IconButton(onClick = {
            onDelete()
        }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color(0xfff23535),
                modifier = iconModifier
            )
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            // Check if the recorder is initialized in case the user hits the trash button before pressing record
            if (isInitialized) {
                recorder.release()
            }
        }
    }
}
