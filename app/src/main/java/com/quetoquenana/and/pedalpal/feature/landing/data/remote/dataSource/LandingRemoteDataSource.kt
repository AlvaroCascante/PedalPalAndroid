package com.quetoquenana.and.pedalpal.feature.landing.data.remote.dataSource

import com.quetoquenana.and.pedalpal.feature.landing.domain.model.LandingPageItem

interface LandingRemoteDataSource {
    suspend fun getLandingItems(): List<LandingPageItem>
}
