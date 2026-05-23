package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject

class GetBikeProfileImageUrlUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(id: String): String? {
        return bikeRepository.getBikeProfileImageUrl(id = id)
    }
}

