package com.example.pulse_feed_app_kotlin.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayerScreen(url: String, onClose: () -> Unit, onError: (String) -> Unit = {}) {

    val context = LocalContext.current
    var isBuffering by remember { mutableStateOf(true) }

    Log.d("DEBUG_VIDEO", "=== VideoPlayerScreen started ===")
    Log.d("DEBUG_VIDEO", "URL: $url")

    val exoPlayer = remember {
        Log.d("DEBUG_VIDEO", "Creating ExoPlayer instance")
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            Log.d("DEBUG_VIDEO", "MediaItem set")

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("DEBUG_VIDEO", "ERROR: ${error.message}")
                    Log.e("DEBUG_VIDEO", "Error code: ${error.errorCode}")
                    Log.e("DEBUG_VIDEO", "Error cause: ${error.cause}")
                    onError(error.cause?.message ?: error.message ?: "Video playback failed")
                }

                override fun onPlaybackStateChanged(state: Int) {
                    val stateName = when (state) {
                        Player.STATE_IDLE -> "IDLE"
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY -> "READY"
                        Player.STATE_ENDED -> "ENDED"
                        else -> "UNKNOWN($state)"
                    }
                    Log.d("DEBUG_VIDEO", "Playback state changed: $stateName")
                    isBuffering = state == Player.STATE_BUFFERING || state == Player.STATE_IDLE
                    if (state == Player.STATE_ENDED) {
                        Log.d("DEBUG_VIDEO", "Video ended, closing")
                        onClose()
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    Log.d("DEBUG_VIDEO", "isPlaying changed: $isPlaying")
                }
            })

            Log.d("DEBUG_VIDEO", "Calling prepare()")
            prepare()
            playWhenReady = true
            Log.d("DEBUG_VIDEO", "playWhenReady = true")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("DEBUG_VIDEO", "Releasing ExoPlayer")
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                Log.d("DEBUG_VIDEO", "Creating PlayerView")
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isBuffering) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            Log.d("DEBUG_VIDEO", "Showing buffering spinner")
        }

        IconButton(
            onClick = {
                Log.d("DEBUG_VIDEO", "Close button tapped")
                onClose()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}
