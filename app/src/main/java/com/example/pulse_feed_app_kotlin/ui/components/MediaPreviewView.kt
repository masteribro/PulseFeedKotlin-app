package com.example.pulse_feed_app_kotlin.ui.components

import androidx.compose.runtime.Composable
import com.example.pulse_feed_app_kotlin.models.FeedItem
import com.example.pulse_feed_app_kotlin.models.MediaType
import com.example.pulse_feed_app_kotlin.viewmodels.HomeViewModel

@Composable
fun MediaPreviewView(item: FeedItem, viewModel: HomeViewModel) {
    when (item.type) {
        MediaType.VIDEO -> VideoPreviewView(mediaUrl = item.mediaUrl, viewModel = viewModel)
        MediaType.AUDIO -> AudioPreviewView(mediaUrl = item.mediaUrl, viewModel = viewModel)
        MediaType.DOCUMENT -> DocumentPreviewView(
            mediaUrl = item.mediaUrl,
            assetName = item.assetName,
            fileName = item.fileName,
            viewModel = viewModel
        )
        MediaType.TEXT -> Unit
    }
}
