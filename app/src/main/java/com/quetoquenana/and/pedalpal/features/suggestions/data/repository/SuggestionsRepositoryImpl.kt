package com.quetoquenana.and.pedalpal.features.suggestions.data.repository

import com.quetoquenana.and.pedalpal.features.suggestions.data.remote.dataSource.SuggestionsRemoteDataSource
import com.quetoquenana.and.pedalpal.features.suggestions.domain.model.Suggestion
import com.quetoquenana.and.pedalpal.features.suggestions.domain.repository.SuggestionsRepository
import javax.inject.Inject

class SuggestionsRepositoryImpl @Inject constructor(
    private val remote: SuggestionsRemoteDataSource
) : SuggestionsRepository {

    override suspend fun getSuggestions(): List<Suggestion> = remote.getSuggestions()
}
