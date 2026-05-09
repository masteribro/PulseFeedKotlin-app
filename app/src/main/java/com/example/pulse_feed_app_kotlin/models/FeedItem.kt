package com.example.pulse_feed_app_kotlin.models

import java.util.UUID

data class FeedItem(
    val id: UUID = UUID.randomUUID(),
    val type: MediaType,
    val title: String,
    val description: String? = null,
    val mediaUrl: String? = null,
    val assetName: String? = null,
    val fileName: String? = null
)
