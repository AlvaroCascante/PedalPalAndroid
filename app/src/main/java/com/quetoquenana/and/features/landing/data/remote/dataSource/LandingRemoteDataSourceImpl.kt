package com.quetoquenana.and.features.landing.data.remote.dataSource

import com.quetoquenana.and.features.landing.domain.model.LandingPageItem
import com.quetoquenana.and.R
import javax.inject.Inject

class LandingRemoteDataSourceImpl @Inject constructor() : LandingRemoteDataSource {
    override suspend fun getLandingItems(): List<com.quetoquenana.and.features.landing.domain.model.LandingPageItem> = listOf(
        _root_ide_package_.com.quetoquenana.and.features.landing.domain.model.LandingPageItem(
            id = "l1",
            title = "Welcome to PedalPal",
            description = "Discover features and get started",
            thumbnailRes = R.drawable.mobi_bike_logo
        ),
        _root_ide_package_.com.quetoquenana.and.features.landing.domain.model.LandingPageItem(
            id = "l2",
            title = "Get a Tune-up",
            description = "Book your maintenance appointment",
            thumbnailRes = null
        ),
        _root_ide_package_.com.quetoquenana.and.features.landing.domain.model.LandingPageItem(
            id = "l3",
            title = "Join Loyalty",
            description = "Earn points with every service",
            thumbnailRes = null
        )
    )
}
