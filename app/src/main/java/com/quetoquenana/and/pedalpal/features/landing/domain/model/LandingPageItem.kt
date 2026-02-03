package com.quetoquenana.and.pedalpal.features.landing.domain.model

/**
 * Landing page item displayed in the vertical list on the home screen.
 */
data class LandingPageItem(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailRes: Int? = null
)
