package com.quetoquenana.and.features.suggestions.data.repository

import com.quetoquenana.and.features.suggestions.data.remote.dataSource.SuggestionsRemoteDataSource
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository
import javax.inject.Inject

class SuggestionsRepositoryImpl @Inject constructor(
    private val remote: com.quetoquenana.and.features.suggestions.data.remote.dataSource.SuggestionsRemoteDataSource
) : com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository {

    override suspend fun getSuggestions(): List<com.quetoquenana.and.features.suggestions.domain.model.Suggestion> = remote.getSuggestions()
}
