package com.quetoquenana.and.pedalpal.features.suggestions.domain.model

/**
 * Simple UI/domain model for a suggestion item.
 */
data class Suggestion(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnailRes: Int? = null
)
