package com.example.pulse_feed_app_kotlin.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pulse_feed_app_kotlin.ui.components.FeedCardView
import com.example.pulse_feed_app_kotlin.viewmodels.HomeState
import com.example.pulse_feed_app_kotlin.viewmodels.HomeViewModel
import com.example.pulse_feed_app_kotlin.viewmodels.errorMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val state by viewModel.state.collectAsState()
    val feedItems by viewModel.feedItems.collectAsState()
    Log.d("DEBUG_HOME", "HomeScreen state = ${state::class.simpleName}")

    if (state is HomeState.VideoViewing) {
        val url = (state as HomeState.VideoViewing).url
        VideoPlayerScreen(
            url = url,
            onClose = { viewModel.dismissVideo() },
            onError = { message -> viewModel.videoError(message) }
        )
        return
    }

    if (state is HomeState.DocumentViewing) {
        val path = (state as HomeState.DocumentViewing).path
        PdfViewerScreen(filePath = path, onClose = { viewModel.dismissDocument() })
        return
    }

    state.errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pulse Feed") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF2F2F7)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(feedItems, key = { it.id.toString() }) { item ->
                FeedCardView(item = item, viewModel = viewModel)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
