package com.quetoquenana.and.features.announcements.data.remote.dto

import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.model.AnnouncementMedia
import java.util.UUID

data class AnnouncementResponseDto(
    val id: UUID,
    val title: String,
    val subTitle: String?,
    val description: String,
    val position: Int?,
    val url: String?,
    val status: String?,
    val mediaUrlResponse: List<MediaUrlResponseDto> = emptyList()
)

data class MediaUrlResponseDto(
    val mediaId: UUID,
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
        status = status,
        media = mediaUrlResponse.map { it.toDomain() }
    )
}

private fun MediaUrlResponseDto.toDomain(): AnnouncementMedia {
    return AnnouncementMedia(
        mediaId = mediaId,
        imageUrl = uploadUrl,
        expiresAt = expiresAt
    )
}
