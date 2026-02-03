package com.quetoquenana.and.pedalpal.feature.suggestions.data.remote.dataSource

import com.quetoquenana.and.pedalpal.feature.suggestions.domain.model.Suggestion

interface SuggestionsRemoteDataSource {
    suspend fun getSuggestions(): List<Suggestion>
}
