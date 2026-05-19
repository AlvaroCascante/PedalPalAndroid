package com.quetoquenana.and.features.profile.data.remote.dto

import com.quetoquenana.and.core.media.MediaReferenceType
import com.quetoquenana.and.features.profile.domain.model.ProfilePhotoUploadRequest
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateProfileMediaRequestDto(
    val isPublic: Boolean,
    val referenceType: String,
    val mediaFiles: List<CreateProfileMediaFileRequestDto>,
)

@JsonClass(generateAdapter = true)
data class CreateProfileMediaFileRequestDto(
    val contentType: String,
    val isPrimary: Boolean,
    val name: String,
    val altText: String,
)

@JsonClass(generateAdapter = true)
data class ProfileMediaFileResponseDto(
    val id: String,
    val contentType: String,
    val provider: String,
    val isPrimary: Boolean,
    val status: String,
    val name: String,
    val altText: String?,
    val url: String,
    val expiresAt: String?,
)

fun ProfilePhotoUploadRequest.toCreateProfileMediaRequestDto(
    referenceType: MediaReferenceType,
): CreateProfileMediaRequestDto {
    return CreateProfileMediaRequestDto(
        isPublic = referenceType.isPublic,
        referenceType = referenceType.apiValue,
        mediaFiles = listOf(
            CreateProfileMediaFileRequestDto(
                contentType = contentType,
                isPrimary = true,
                name = name,
                altText = altText,
            )
        ),
    )
}

fun List<ProfileMediaFileResponseDto>.primaryImage(): ProfileMediaFileResponseDto? {
    return firstOrNull {
        it.contentType.startsWith(prefix = "IMAGE_") &&
            it.isPrimary &&
            it.url.isNotBlank()
    } ?: firstOrNull {
        it.contentType.startsWith(prefix = "IMAGE_") && it.url.isNotBlank()
    }
}

