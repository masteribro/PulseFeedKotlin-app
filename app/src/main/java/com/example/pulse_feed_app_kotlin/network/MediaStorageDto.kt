package com.example.pulse_feed_app_kotlin.network

import com.google.gson.annotations.SerializedName

data class MediaStorageDto(
    val id: Int,
    val url: String,
    val text: String,
    @SerializedName("mediaType") val mediaType: String
)
