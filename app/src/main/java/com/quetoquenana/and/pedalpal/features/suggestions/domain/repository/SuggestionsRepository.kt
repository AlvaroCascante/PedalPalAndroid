package com.quetoquenana.and.pedalpal.features.suggestions.domain.repository

import com.quetoquenana.and.pedalpal.features.suggestions.domain.model.Suggestion

interface SuggestionsRepository {
    suspend fun getSuggestions(): List<Suggestion>
}
