package com.example.pulse_feed_app_kotlin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pulse_feed_app_kotlin.viewmodels.HomeState
import com.example.pulse_feed_app_kotlin.viewmodels.HomeViewModel

@Composable
fun DocumentPreviewView(
    mediaUrl: String?,
    assetName: String?,
    fileName: String?,
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsState()
    val isLoading = state is HomeState.DocumentLoading && (state as HomeState.DocumentLoading).isLoading

    if (isLoading) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(color = Color.White)
            Text(
                text = "Loading document...",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                when {
                    assetName != null && fileName != null ->
                        viewModel.viewAssetDocument(assetName, fileName)
                    mediaUrl != null && fileName != null ->
                        viewModel.viewDocument(mediaUrl, fileName)
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "Document",
                tint = Color.Red,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "View PDF",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
