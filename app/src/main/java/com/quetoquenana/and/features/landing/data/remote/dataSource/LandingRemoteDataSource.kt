package com.quetoquenana.and.features.landing.data.remote.dataSource

import com.quetoquenana.and.features.landing.domain.model.LandingPageItem

interface LandingRemoteDataSource {
    suspend fun getLandingItems(): List<com.quetoquenana.and.features.landing.domain.model.LandingPageItem>
}
