package com.quetoquenana.and.features.landing.domain.usecase

import com.quetoquenana.and.features.landing.domain.model.LandingPageItem
import com.quetoquenana.and.features.landing.domain.repository.LandingRepository
import javax.inject.Inject

class GetLandingItemsUseCase @Inject constructor(
    private val repository: com.quetoquenana.and.features.landing.domain.repository.LandingRepository
) {
    suspend operator fun invoke(): List<com.quetoquenana.and.features.landing.domain.model.LandingPageItem> = repository.getLandingItems()
}
