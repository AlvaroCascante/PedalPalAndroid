package com.quetoquenana.and.features.home.domain.fakes

import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository

class FakeSuggestionsRepository(
    private val suggestions: List<Suggestion> = emptyList()
) : SuggestionsRepository {
    override suspend fun getSuggestions(): List<Suggestion> = suggestions
}
