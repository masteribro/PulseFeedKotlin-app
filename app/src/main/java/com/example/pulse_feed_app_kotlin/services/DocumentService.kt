package com.example.pulse_feed_app_kotlin.services

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class DocumentService(private val context: Context) {


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    suspend fun downloadDocument(url: String, fileName: String): Result<String> {
        _isLoading.value = true

        return try {

            val destFile = File(context.filesDir, fileName)


            withContext(Dispatchers.IO) {

                URL(url).openStream().use { input ->


                    FileOutputStream(destFile).use { output ->


                        input.copyTo(output)
                    }
                }
            }

            _isLoading.value = false
            Result.success(destFile.absolutePath)

        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }
}