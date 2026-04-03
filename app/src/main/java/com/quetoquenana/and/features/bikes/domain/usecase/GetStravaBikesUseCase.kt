package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject

class GetStravaBikesUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(): List<StravaBike> = bikeRepository.getStravaBikes()
}

