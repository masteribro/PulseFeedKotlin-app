package com.example.pulse_feed_app_kotlin.services

import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioPlayerService {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    fun play(url: String) {
        stop()
        _isLoading.value = true
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            setOnPreparedListener {
                _isLoading.value = false
                start()
                _isPlaying.value = true
            }
            setOnCompletionListener {
                _isPlaying.value = false
                _isLoading.value = false
            }
            setOnErrorListener { _, _, _ ->
                _isPlaying.value = false
                _isLoading.value = false
                true
            }
            prepareAsync()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun stop() {
        mediaPlayer?.apply {
            try {
                if (isPlaying) stop()
            } catch (_: Exception) {}
            reset()
            release()
        }
        mediaPlayer = null
        _isPlaying.value = false
        _isLoading.value = false
    }
}
