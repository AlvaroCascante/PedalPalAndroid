package com.quetoquenana.and.core.media.domain.model

import com.squareup.moshi.JsonClass
import java.time.Instant
import java.util.UUID

fun List<MediaUploadRequest>.toCreateMediaRequestDto(
    referenceType: String,
    isPublic: Boolean = false
): CreateMediaRequestDto {
    return CreateMediaRequestDto(
        isPublic = isPublic,
        referenceType = referenceType,
        mediaFiles = map { upload ->
            CreateMediaFileRequestDto(
                id = upload.correlationId,
                contentType = upload.contentType,
                name = upload.name,
                altText = upload.altText,
            )
        },
    )
}

@JsonClass(generateAdapter = true)
data class CreateMediaRequestDto(
    val isPublic: Boolean,
    val referenceType: String,
    val mediaFiles: List<CreateMediaFileRequestDto>,
)

@JsonClass(generateAdapter = true)
data class CreateMediaFileRequestDto(
    val id: UUID,
    val contentType: String,
    val name: String,
    val altText: String,
)

@JsonClass(generateAdapter = true)
data class MediaFileResponseDto(
    val id: UUID,
    val correlationId: UUID?,
    val contentType: String,
    val provider: String,
    val status: String,
    val name: String,
    val altText: String?,
    val url: String,
    val expiresAt: Instant?,
    val isPublic: Boolean
)

fun MediaFileResponseDto.toDomain(
    referenceId: UUID,
    referenceType: MediaReferenceType,
    currentTime: Long = System.currentTimeMillis()
): MediaAsset {
    return MediaAsset(
        referenceId = referenceId,
        referenceType = referenceType,
        mediaId = id,
        url = url,
        contentType = contentType,
        name = name,
        altText = altText,
        isPrivate = !isPublic,
        urlExpireAt = expiresAt,
        updatedAt = currentTime,
        fetchedAt = currentTime
    )
}

fun List<MediaFileResponseDto>.toDomain(
    referenceId: UUID,
    referenceType: MediaReferenceType
): List<MediaAsset> {
    val currentTime = System.currentTimeMillis()
    return map { media ->
        media.toDomain(
            referenceId = referenceId,
            referenceType = referenceType,
            currentTime = currentTime
        )
    }
}

fun MutableList<MediaFileResponseDto>.takeMatchingMedia(
    upload: MediaUploadRequest,
): MediaFileResponseDto? {
    val matchIndex = indexOfFirst { it.correlationId == upload.correlationId }

    if (matchIndex < 0) return null
    return removeAt(matchIndex)
}