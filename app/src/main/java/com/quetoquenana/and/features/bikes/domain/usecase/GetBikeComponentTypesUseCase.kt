package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.ComponentType
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject

class GetBikeComponentTypesUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(): List<ComponentType> {
        return bikeRepository.getBikeComponentTypes()
    }
}
