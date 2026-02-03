package com.quetoquenana.and.pedalpal.feature.landing.data.remote.dataSource

import com.quetoquenana.and.pedalpal.feature.landing.domain.model.LandingPageItem
import com.quetoquenana.and.pedalpal.R
import javax.inject.Inject

class LandingRemoteDataSourceImpl @Inject constructor() : LandingRemoteDataSource {
    override suspend fun getLandingItems(): List<LandingPageItem> = listOf(
        LandingPageItem(id = "l1", title = "Welcome to PedalPal", description = "Discover features and get started", thumbnailRes = R.drawable.mobi_bike_logo),
        LandingPageItem(id = "l2", title = "Get a Tune-up", description = "Book your maintenance appointment", thumbnailRes = null),
        LandingPageItem(id = "l3", title = "Join Loyalty", description = "Earn points with every service", thumbnailRes = null)
    )
}
