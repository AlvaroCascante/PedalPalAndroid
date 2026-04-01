package com.quetoquenana.and.features.announcements.domain.model

/**
 * Landing page item displayed in the vertical list on the home screen.
 */
data class Announcement(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailRes: Int? = null
)
