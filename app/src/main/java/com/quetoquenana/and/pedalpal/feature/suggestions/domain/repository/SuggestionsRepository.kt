package com.quetoquenana.and.pedalpal.feature.suggestions.domain.repository

import com.quetoquenana.and.pedalpal.feature.suggestions.domain.model.Suggestion

interface SuggestionsRepository {
    suspend fun getSuggestions(): List<Suggestion>
}
