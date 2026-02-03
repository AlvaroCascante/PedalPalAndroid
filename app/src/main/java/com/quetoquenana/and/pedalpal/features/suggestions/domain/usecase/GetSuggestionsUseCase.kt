package com.quetoquenana.and.pedalpal.features.suggestions.domain.usecase

import com.quetoquenana.and.pedalpal.features.suggestions.domain.model.Suggestion
import com.quetoquenana.and.pedalpal.features.suggestions.domain.repository.SuggestionsRepository
import javax.inject.Inject

class GetSuggestionsUseCase @Inject constructor(
    private val repository: SuggestionsRepository
) {
    suspend operator fun invoke(): List<Suggestion> = repository.getSuggestions()
}
