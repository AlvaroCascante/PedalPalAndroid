package com.quetoquenana.and.pedalpal.feature.landing.domain.repository

import com.quetoquenana.and.pedalpal.feature.landing.domain.model.LandingPageItem

interface LandingRepository {
    suspend fun getLandingItems(): List<LandingPageItem>
}
