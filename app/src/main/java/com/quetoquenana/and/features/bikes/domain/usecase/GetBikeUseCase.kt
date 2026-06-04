package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import java.util.UUID
import javax.inject.Inject

class GetBikeUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(id: UUID): Bike = bikeRepository.getBike(id)
}
