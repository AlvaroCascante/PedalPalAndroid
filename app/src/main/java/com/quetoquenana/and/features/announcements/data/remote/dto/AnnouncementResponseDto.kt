package com.quetoquenana.and.features.announcements.data.remote.dto

import com.quetoquenana.and.features.announcements.domain.model.Announcement

data class AnnouncementResponseDto(
    val id: String,
    val title: String,
    val subTitle: String?,
    val description: String,
    val position: Int?,
    val url: String?,
    val status: String?,
    val mediaUrlResponse: Set<MediaUrlResponseDto> = emptySet()
)

data class MediaUrlResponseDto(
    val mediaId: String,
    val uploadUrl: String,
    val expiresAt: String
)

fun AnnouncementResponseDto.toDomain(): Announcement {
    return Announcement(
        id = id,
        title = title,
        subTitle = subTitle,
        description = description,
        position = position,
        url = url,
        status = status
    )
}
