package com.quetoquenana.and.features.landing.domain.repository

import com.quetoquenana.and.features.landing.domain.model.LandingPageItem

interface LandingRepository {
    suspend fun getLandingItems(): List<LandingPageItem>
}
