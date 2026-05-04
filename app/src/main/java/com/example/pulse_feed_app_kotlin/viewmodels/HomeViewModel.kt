package com.example.pulse_feed_app_kotlin.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulse_feed_app_kotlin.models.FeedItem
import com.example.pulse_feed_app_kotlin.services.AssetDocumentService
import com.example.pulse_feed_app_kotlin.services.AudioPlayerService
import com.example.pulse_feed_app_kotlin.services.DocumentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class HomeState {
    data object Initial : HomeState()
    data class AudioPlaying(val isPlaying: Boolean) : HomeState()
    data class VideoViewing(val url: String) : HomeState()
    data class DocumentLoading(val isLoading: Boolean) : HomeState()
    data class DocumentViewing(val path: String) : HomeState()
    data class Error(val message: String) : HomeState()
}

val HomeState.errorMessage: String?
    get() = if (this is HomeState.Error) message else null

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<HomeState>(HomeState.Initial)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    val feedItems = FeedItem.sampleItems

    val audioService = AudioPlayerService()
    private val documentService = DocumentService(application)
    private val assetDocumentService = AssetDocumentService(application)

    init {
        viewModelScope.launch {
            audioService.isPlaying.collect { isPlaying ->
                val current = _state.value
                if (current !is HomeState.VideoViewing && current !is HomeState.DocumentViewing) {
                    Log.d("DEBUG_VM", "Audio isPlaying changed: $isPlaying, updating state")
                    _state.value = HomeState.AudioPlaying(isPlaying)
                } else {
                    Log.d("DEBUG_VM", "Audio isPlaying=$isPlaying but state is $current — ignoring")
                }
            }
        }
    }

    fun playAudio(url: String) = audioService.play(url)

    fun pauseAudio() = audioService.pause()

    fun stopAudio() = audioService.stop()

    fun playVideo(url: String) {
        Log.d("DEBUG_VM", "playVideo called, url=$url")
        Log.d("DEBUG_VM", "State before: ${_state.value}")
        _state.value = HomeState.VideoViewing(url)
        Log.d("DEBUG_VM", "State after: ${_state.value}")
    }

    fun stopVideo() {
        _state.value = HomeState.Initial
    }

    fun videoError(message: String) {
        _state.value = HomeState.Error(message)
    }

    fun viewAssetDocument(assetName: String, fileName: String) {
        Log.d("DEBUG_VM", "viewAssetDocument called: assetName=$assetName fileName=$fileName")
        _state.value = HomeState.DocumentLoading(true)
        viewModelScope.launch {
            val file = withContext(Dispatchers.IO) {
                assetDocumentService.getAssetDocumentPath(assetName)
            }
            Log.d("DEBUG_VM", "getAssetDocumentPath result: ${file?.absolutePath}")
            if (file != null) {
                Log.d("DEBUG_VM", "PDF found, setting DocumentViewing state")
                _state.value = HomeState.DocumentViewing(file.absolutePath)
                Log.d("DEBUG_VM", "State is now: ${_state.value}")
            } else {
                Log.e("DEBUG_VM", "PDF NOT found in assets for assetName=$assetName")
                _state.value = HomeState.Error("PDF not found. Place \"$fileName\" in app/src/main/assets/")
            }
        }
    }

    fun viewDocument(url: String, fileName: String) {
        _state.value = HomeState.DocumentLoading(true)
        viewModelScope.launch {
            documentService.downloadDocument(url, fileName).fold(
                onSuccess = { path ->
                    _state.value = HomeState.DocumentViewing(path)
                },
                onFailure = { e ->
                    _state.value = HomeState.Error(e.message ?: "Download failed")
                }
            )
        }
    }

    fun dismissError() {
        _state.value = HomeState.Initial
    }

    fun dismissDocument() {
        _state.value = HomeState.Initial
    }

    fun dismissVideo() {
        _state.value = HomeState.Initial
    }

    override fun onCleared() {
        super.onCleared()
        audioService.stop()
    }
}
