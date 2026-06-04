package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import java.util.UUID
import javax.inject.Inject

class GetBikeMediaUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(id: UUID): List<BikeMedia> = bikeRepository.getBikeMedia(id)
}
