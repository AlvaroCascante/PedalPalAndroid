package com.quetoquenana.and.pedalpal.features.landing.data.remote.dataSource

import com.quetoquenana.and.pedalpal.features.landing.domain.model.LandingPageItem

interface LandingRemoteDataSource {
    suspend fun getLandingItems(): List<LandingPageItem>
}
