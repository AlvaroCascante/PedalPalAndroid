package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveBikesUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    operator fun invoke(): Flow<List<Bike>> = bikeRepository.observeBikes()
}
