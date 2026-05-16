package com.example.pulse_feed_app_kotlin.network

import retrofit2.http.GET

interface MediaApiService {
    @GET("api/v1/media-data")
    suspend fun getMediaItems(): List<MediaStorageDto>
}
