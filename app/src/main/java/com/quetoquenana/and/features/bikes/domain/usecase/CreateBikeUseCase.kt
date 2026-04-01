package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject

class CreateBikeUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(request: CreateBikeRequest): Bike {
        return bikeRepository.createBike(request)
    }
}
