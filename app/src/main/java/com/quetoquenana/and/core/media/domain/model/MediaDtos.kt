package com.quetoquenana.and.core.media.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateMediaRequestDto(
    val isPublic: Boolean,
    val referenceType: String,
    val mediaFiles: List<CreateMediaFileRequestDto>,
)

@JsonClass(generateAdapter = true)
data class CreateMediaFileRequestDto(
    val contentType: String,
    val name: String,
    val altText: String,
)

@JsonClass(generateAdapter = true)
data class MediaFileResponseDto(
    val id: String,
    val contentType: String,
    val provider: String,
    val status: String,
    val name: String,
    val altText: String?,
    val url: String,
    val expiresAt: Long?,
    val isPublic: Boolean
)

fun List<MediaUploadRequest>.toCreateMediaRequestDto(
    referenceType: String,
    isPublic: Boolean = false
): CreateMediaRequestDto {
    return CreateMediaRequestDto(
        isPublic = isPublic,
        referenceType = referenceType,
        mediaFiles = map { upload ->
            CreateMediaFileRequestDto(
                contentType = upload.contentType,
                name = upload.name,
                altText = upload.altText,
            )
        },
    )
}

