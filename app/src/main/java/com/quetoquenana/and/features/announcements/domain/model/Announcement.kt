package com.quetoquenana.and.features.announcements.domain.model

/**
 * Landing page item displayed in the vertical list on the home screen.
 */
data class Announcement(
    val id: String,
    val title: String,
    val subTitle: String? = null,
    val description: String,
    val position: Int? = null,
    val url: String? = null,
    val status: String? = null,
    val thumbnailRes: Int? = null
)
