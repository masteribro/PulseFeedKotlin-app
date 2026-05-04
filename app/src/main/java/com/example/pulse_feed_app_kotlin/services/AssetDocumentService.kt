package com.example.pulse_feed_app_kotlin.services

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class AssetDocumentService(private val context: Context) {

    fun getAssetDocumentPath(fileName: String): File? {
        val candidates = buildList {
            add(fileName)
            if (!fileName.endsWith(".pdf")) add("$fileName.pdf")
            add(fileName.removeSuffix(".pdf"))
        }.distinct()

        Log.d("AssetDocumentService", "Looking for: $fileName")
        Log.d("AssetDocumentService", "Candidates: $candidates")

        val assetsList = try {
            context.assets.list("") ?: emptyArray()
        } catch (e: Exception) {
            Log.e("AssetDocumentService", "Failed to list assets: ${e.message}")
            return null
        }

        Log.d("AssetDocumentService", "All assets: ${assetsList.toList()}")

        for (candidate in candidates) {
            val match = assetsList.firstOrNull { it == candidate || it == "$candidate.pdf" }
            Log.d("AssetDocumentService", "Candidate '$candidate' -> match: $match")

            if (match == null) continue

            return try {
                val destFile = File(context.cacheDir, match)
                Log.d("AssetDocumentService", "Dest file: ${destFile.absolutePath}, exists: ${destFile.exists()}")

                if (!destFile.exists()) {
                    Log.d("AssetDocumentService", "Copying from assets...")
                    context.assets.open(match).use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.d("AssetDocumentService", "Copy done. File size: ${destFile.length()} bytes")
                }

                destFile
            } catch (e: Exception) {
                Log.e("AssetDocumentService", "Error copying asset: ${e.message}", e)
                null
            }
        }

        Log.e("AssetDocumentService", "No matching asset found for: $fileName")
        return null
    }

    fun listAvailableDocuments(): List<String> {
        return try {
            context.assets.list("")
                ?.filter { it.endsWith(".pdf") }
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
