package com.quetoquenana.and.pedalpal.feature.suggestions.domain.usecase

import com.quetoquenana.and.pedalpal.feature.suggestions.domain.model.Suggestion
import com.quetoquenana.and.pedalpal.feature.suggestions.domain.repository.SuggestionsRepository
import javax.inject.Inject

class GetSuggestionsUseCase @Inject constructor(
    private val repository: SuggestionsRepository
) {
    suspend operator fun invoke(): List<Suggestion> = repository.getSuggestions()
}
