package com.example.pulse_feed_app_kotlin.ui.screens

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PdfViewerScreen(filePath: String, onClose: () -> Unit) {
    var renderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var pfd by remember { mutableStateOf<ParcelFileDescriptor?>(null) }
    var pageCount by remember { mutableStateOf(0) }
    var showControls by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Log.d("DEBUG_PDF", "=== PdfViewerScreen started ===")
    Log.d("DEBUG_PDF", "filePath: $filePath")

    LaunchedEffect(filePath) {
        val file = File(filePath)
        Log.d("DEBUG_PDF", "File exists: ${file.exists()}")
        Log.d("DEBUG_PDF", "File size: ${file.length()} bytes")
        Log.d("DEBUG_PDF", "File can read: ${file.canRead()}")

        if (!file.exists()) {
            Log.e("DEBUG_PDF", "File does NOT exist — calling onClose()")
            onClose()
            return@LaunchedEffect
        }

        withContext(Dispatchers.IO) {
            try {
                Log.d("DEBUG_PDF", "Opening ParcelFileDescriptor...")
                val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                Log.d("DEBUG_PDF", "ParcelFileDescriptor opened successfully")

                val pdfRenderer = PdfRenderer(descriptor)
                Log.d("DEBUG_PDF", "PdfRenderer created successfully")
                Log.d("DEBUG_PDF", "Page count: ${pdfRenderer.pageCount}")

                withContext(Dispatchers.Main) {
                    pfd = descriptor
                    renderer = pdfRenderer
                    pageCount = pdfRenderer.pageCount
                    Log.d("DEBUG_PDF", "State updated on Main thread: pageCount=$pageCount")
                }
            } catch (e: Exception) {
                Log.e("DEBUG_PDF", "EXCEPTION opening PDF: ${e::class.simpleName}: ${e.message}")
                Log.e("DEBUG_PDF", "Stack: ${e.stackTraceToString()}")
                withContext(Dispatchers.Main) {
                    errorMessage = e.message ?: "Failed to open PDF"
                }
            }
        }
    }

    DisposableEffect(filePath) {
        onDispose {
            Log.d("DEBUG_PDF", "Disposing — closing renderer and pfd")
            renderer?.close()
            pfd?.close()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val err = errorMessage
        val currentRenderer = renderer

        when {
            err != null -> {
                Log.d("DEBUG_PDF", "Showing error: $err")
                Text(text = "Error: $err", color = Color.Red)
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 16.dp)
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            currentRenderer == null || pageCount == 0 -> {
                Log.d("DEBUG_PDF", "Still loading — showing spinner (renderer=$currentRenderer, pageCount=$pageCount)")
                CircularProgressIndicator(color = Color.White)
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 16.dp)
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            else -> {
                Log.d("DEBUG_PDF", "Renderer ready, showing pager with $pageCount pages")
                val pagerState = rememberPagerState(pageCount = { pageCount })
                val scope = rememberCoroutineScope()

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    PdfPageView(
                        renderer = currentRenderer,
                        pageIndex = pageIndex,
                        onTap = { showControls = !showControls }
                    )
                }

                AnimatedVisibility(visible = showControls) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 48.dp, end = 16.dp)
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 32.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                },
                                enabled = pagerState.currentPage > 0,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowLeft,
                                    contentDescription = "Previous page",
                                    tint = Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${pagerState.currentPage + 1} / $pageCount",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            IconButton(
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                                enabled = pagerState.currentPage < pageCount - 1,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Next page",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfPageView(
    renderer: PdfRenderer,
    pageIndex: Int,
    onTap: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 3f)
        if (scale > 1f) offset += offsetChange else offset = Offset.Zero
    }

    LaunchedEffect(pageIndex) {
        Log.d("DEBUG_PDF", "Rendering page $pageIndex...")
        withContext(Dispatchers.IO) {
            try {
                val bmp = synchronized(renderer) {
                    renderer.openPage(pageIndex).use { page ->
                        val width = page.width * 2
                        val height = page.height * 2
                        Log.d("DEBUG_PDF", "Page $pageIndex size: ${width}x${height}")
                        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        b.eraseColor(android.graphics.Color.WHITE)
                        page.render(b, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        b
                    }
                }
                withContext(Dispatchers.Main) {
                    bitmap = bmp
                    Log.d("DEBUG_PDF", "Page $pageIndex bitmap set on Main thread")
                }
            } catch (e: Exception) {
                Log.e("DEBUG_PDF", "EXCEPTION rendering page $pageIndex: ${e.message}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { onTap() } },
        contentAlignment = Alignment.Center
    ) {
        val bmp = bitmap
        if (bmp != null) {
            Log.d("DEBUG_PDF", "Displaying bitmap for page $pageIndex")
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Page ${pageIndex + 1}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(state = transformableState)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
        } else {
            Log.d("DEBUG_PDF", "Bitmap for page $pageIndex is null — showing page spinner")
            CircularProgressIndicator(color = Color.White)
        }
    }
}
