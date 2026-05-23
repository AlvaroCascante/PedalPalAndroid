package com.quetoquenana.and.features.bikes.domain.model

import com.quetoquenana.and.core.media.domain.model.MediaAsset

data class BikeMedia(
    val id: String,
    val contentType: String,
    val provider: String,
    val name: String,
    val altText: String?,
    val url: String,
    val expiresAt: String?
)

fun MediaAsset.toBikeMedia(): BikeMedia {
    return BikeMedia(
        id = this.mediaId,
        contentType = this.contentType,
        provider = if (this.isPrivate) "Private" else "Public",
        name = this.name,
        altText = this.altText,
        url = this.url,
        expiresAt = null,
    )
}

fun List<MediaAsset>.toBikeMedia(): List<BikeMedia> {
    return asSequence()
        .filter { media ->
            media.contentType.startsWith(prefix = "IMAGE_") ||
                media.contentType.startsWith(prefix = "image/")
        }
        .map { media -> media.toBikeMedia() }
        .toList()
}

