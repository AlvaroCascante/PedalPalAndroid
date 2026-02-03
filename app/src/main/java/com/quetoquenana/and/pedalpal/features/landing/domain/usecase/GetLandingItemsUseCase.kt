package com.quetoquenana.and.pedalpal.features.landing.domain.usecase

import com.quetoquenana.and.pedalpal.features.landing.domain.model.LandingPageItem
import com.quetoquenana.and.pedalpal.features.landing.domain.repository.LandingRepository
import javax.inject.Inject

class GetLandingItemsUseCase @Inject constructor(
    private val repository: LandingRepository
) {
    suspend operator fun invoke(): List<LandingPageItem> = repository.getLandingItems()
}
