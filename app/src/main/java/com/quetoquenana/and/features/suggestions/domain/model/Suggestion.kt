package com.quetoquenana.and.features.suggestions.domain.model

import java.util.UUID

/**
 * Simple UI/domain model for a suggestion item.
 */
data class Suggestion(
    val id: UUID,
    val title: String,
    val subtitle: String,
    val thumbnailRes: Int? = null
)
