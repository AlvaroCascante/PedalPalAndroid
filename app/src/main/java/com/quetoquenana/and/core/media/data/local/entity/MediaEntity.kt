package com.quetoquenana.and.core.media.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import java.time.Instant
import java.util.UUID

@Entity(
    tableName = "media_asset",
    primaryKeys = ["referenceId", "referenceType", "mediaId"],
    indices = [
        Index(value = ["referenceId", "referenceType"]),
        Index(value = ["mediaId"]),
    ],
)
data class MediaEntity(
    val referenceId: UUID,
    val referenceType: String,
    val mediaId: UUID,
    val url: String,
    val urlExpireAt: Instant?,
    val contentType: String,
    val name: String,
    val altText: String?,
    val isPrivate: Boolean,
    val updatedAt: Long,
    val fetchedAt: Long,
)

fun MediaEntity.toDomain(): MediaAsset {
    return MediaAsset(
        referenceId = referenceId,
        referenceType = MediaReferenceType.valueOf(referenceType),
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
fun MediaEntity.requiresRefresh(currentTime: Instant = Instant.now()): Boolean {
    return urlExpireAt?.let { expiresAt -> !expiresAt.isAfter(currentTime) } == true
}

fun List<MediaEntity>.requiresRefresh(currentTime: Instant = Instant.now()): Boolean {
    if (isEmpty()) { return true }
    return any { _ -> requiresRefresh(currentTime = currentTime) }
}