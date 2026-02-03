package com.quetoquenana.and.pedalpal.feature.landing.domain.usecase

import com.quetoquenana.and.pedalpal.feature.landing.domain.model.LandingPageItem
import com.quetoquenana.and.pedalpal.feature.landing.domain.repository.LandingRepository
import javax.inject.Inject

class GetLandingItemsUseCase @Inject constructor(
    private val repository: LandingRepository
) {
    suspend operator fun invoke(): List<LandingPageItem> = repository.getLandingItems()
}
