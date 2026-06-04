package com.quetoquenana.and.core.media.domain.model

import com.quetoquenana.and.core.media.data.local.entity.MediaEntity
import java.time.Instant
import java.util.UUID

data class MediaAsset(
    val referenceId: UUID,
    val referenceType: MediaReferenceType,
    val mediaId: UUID,
    val url: String,
    val contentType: String,
    val name: String,
    val altText: String?,
    val isPrivate: Boolean = true,
    val urlExpireAt: Instant?,
    val updatedAt: Long,
    val fetchedAt: Long
)

fun MediaAsset.toEntity(): MediaEntity {
    return MediaEntity(
        referenceId = referenceId,
        referenceType = referenceType.name,
        mediaId = mediaId,
        url = url,
        urlExpireAt = urlExpireAt,
        contentType = contentType,
        name = name,
        altText = altText,
        isPrivate = isPrivate,
        updatedAt = updatedAt,
        fetchedAt = fetchedAt
    )
}