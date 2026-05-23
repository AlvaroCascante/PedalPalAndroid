package com.quetoquenana.and.core.media.domain.model

data class MediaAsset(
    val referenceId: String,
    val referenceType: MediaReferenceType,
    val mediaId: String,
    val url: String,
    val contentType: String,
    val name: String,
    val altText: String?,
    val isPrivate: Boolean = true,
    val urlExpireAt: Long?,
    val updatedAt: Long,
    val fetchedAt: Long
)

fun List<MediaAsset>.primaryImage(): MediaAsset? {
    return firstOrNull { media ->
        media.contentType.startsWith(prefix = "IMAGE_") && media.url.isNotBlank()
    }
}
