package com.quetoquenana.and.features.announcements.domain.model

import java.util.UUID

/**
 * Landing page item displayed in the vertical list on the home screen.
 */
data class Announcement(
    val id: UUID,
    val title: String,
    val subTitle: String? = null,
    val description: String,
    val position: Int? = null,
    val url: String? = null,
    val status: String? = null,
    val media: List<AnnouncementMedia> = emptyList(),
    val thumbnailRes: Int? = null
)

data class AnnouncementMedia(
    val mediaId: UUID,
    val imageUrl: String,
    val expiresAt: String? = null
)
