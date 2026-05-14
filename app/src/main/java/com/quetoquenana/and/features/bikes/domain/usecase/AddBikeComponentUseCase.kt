package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.AddBikeComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.BikeComponent
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject

class AddBikeComponentUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(
        bikeId: String,
        request: AddBikeComponentRequest
    ): BikeComponent {
        return bikeRepository.addBikeComponent(bikeId = bikeId, request = request)
    }
}
