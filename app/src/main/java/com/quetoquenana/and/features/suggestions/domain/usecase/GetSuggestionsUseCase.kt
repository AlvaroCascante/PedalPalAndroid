package com.quetoquenana.and.features.suggestions.domain.usecase

import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository
import javax.inject.Inject

class GetSuggestionsUseCase @Inject constructor(
    private val repository: com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository
) {
    suspend operator fun invoke(): List<com.quetoquenana.and.features.suggestions.domain.model.Suggestion> = repository.getSuggestions()
}
