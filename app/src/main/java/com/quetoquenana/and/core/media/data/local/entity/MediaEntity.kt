package com.quetoquenana.and.core.media.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType

@Entity(
    tableName = "media_assets",
    primaryKeys = ["referenceId", "referenceType", "mediaId"],
    indices = [
        Index(value = ["referenceId", "referenceType"]),
        Index(value = ["mediaId"]),
    ],
)
data class MediaEntity(
    val referenceId: String,
    val referenceType: String,
    val mediaId: String,
    val url: String,
    val urlExpireAt: Long?,
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

