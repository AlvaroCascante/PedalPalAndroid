package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import java.util.UUID
import javax.inject.Inject

class GetBikeHistoryUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(id: UUID): List<BikeHistory> = bikeRepository.getBikeHistory(id)
}
