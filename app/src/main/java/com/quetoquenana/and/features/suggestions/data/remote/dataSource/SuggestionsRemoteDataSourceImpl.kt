package com.quetoquenana.and.features.suggestions.data.remote.dataSource

import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import com.quetoquenana.and.R
import javax.inject.Inject

class SuggestionsRemoteDataSourceImpl @Inject constructor() : SuggestionsRemoteDataSource {
    override suspend fun getSuggestions(): List<Suggestion> = listOf(
        Suggestion(
            id = "s1",
            title = "Helmet Discount",
            subtitle = "10% off helmets this week",
            thumbnailRes = R.drawable.mobi_bike_logo
        ),
        Suggestion(
            id = "s2",
            title = "Tune-up Offer",
            subtitle = "Free check with subscription",
            thumbnailRes = null
        ),
        Suggestion(
            id = "s3",
            title = "New Tires",
            subtitle = "Recommended for your bike",
            thumbnailRes = null
        )
    )
}
