package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.AddComponentRequest
import com.quetoquenana.and.features.bikes.domain.model.Component
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import java.util.UUID
import javax.inject.Inject

class AddBikeComponentUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(
        bikeId: UUID,
        request: AddComponentRequest
    ): Component {
        return bikeRepository.addBikeComponent(bikeId = bikeId, request = request)
    }
}
