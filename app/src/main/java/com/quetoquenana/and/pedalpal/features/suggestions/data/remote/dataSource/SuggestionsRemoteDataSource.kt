package com.quetoquenana.and.pedalpal.features.suggestions.data.remote.dataSource

import com.quetoquenana.and.pedalpal.features.suggestions.domain.model.Suggestion

interface SuggestionsRemoteDataSource {
    suspend fun getSuggestions(): List<Suggestion>
}
