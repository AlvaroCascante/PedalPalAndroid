package com.quetoquenana.and.features.suggestions.domain.repository

import com.quetoquenana.and.features.suggestions.domain.model.Suggestion

interface SuggestionsRepository {
    suspend fun getSuggestions(): List<Suggestion>
}
