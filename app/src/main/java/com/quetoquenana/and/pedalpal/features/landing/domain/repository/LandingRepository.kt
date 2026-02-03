package com.quetoquenana.and.pedalpal.features.landing.domain.repository

import com.quetoquenana.and.pedalpal.features.landing.domain.model.LandingPageItem

interface LandingRepository {
    suspend fun getLandingItems(): List<LandingPageItem>
}
