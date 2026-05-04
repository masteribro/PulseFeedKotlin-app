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
) {
    companion object {
        val sampleItems = listOf(
            FeedItem(
                type = MediaType.VIDEO,
                title = "VideoChannel",
                description = "Watch this cute cat doing tricks \uD83D\uDC31 #Cats #Funny",
                mediaUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
            ),
            FeedItem(
                type = MediaType.AUDIO,
                title = "PodcastDaily",
                description = "Start your day with this amazing podcast \u2600\uFE0F #MorningMotivation",
                mediaUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
            ),
            FeedItem(
                type = MediaType.DOCUMENT,
                title = "My CV",
                description = "Mohamed Ibrahim's CV \uD83D\uDCC4",
                assetName = "Mohammed_Ibrahim_CV",
                fileName = "Mohamed_Ibrahim_CV.pdf"
            ),
            FeedItem(
                type = MediaType.TEXT,
                title = "DailyThoughts",
                description = "Just finished building this awesome app! \uD83D\uDE80\n\nFeeling proud of what we've accomplished. The journey of learning Kotlin has been amazing.\n\n#AndroidDev #MobileApps #CodingLife"
            ),
            FeedItem(
                type = MediaType.TEXT,
                title = "WeatherUpdate",
                description = "Beautiful sunny day here in California! \u2600\uFE0F 75\u00B0F and perfect for coding."
            ),
            FeedItem(
                type = MediaType.TEXT,
                title = "TechNews",
                description = "Breaking: New Android version just dropped! Check out the amazing new features \uD83D\uDD25"
            )
        )
    }
}
