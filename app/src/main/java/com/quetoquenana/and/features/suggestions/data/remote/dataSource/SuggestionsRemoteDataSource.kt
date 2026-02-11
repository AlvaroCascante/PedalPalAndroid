package com.quetoquenana.and.features.suggestions.data.remote.dataSource

import com.quetoquenana.and.features.suggestions.domain.model.Suggestion

interface SuggestionsRemoteDataSource {
    suspend fun getSuggestions(): List<com.quetoquenana.and.features.suggestions.domain.model.Suggestion>
}
