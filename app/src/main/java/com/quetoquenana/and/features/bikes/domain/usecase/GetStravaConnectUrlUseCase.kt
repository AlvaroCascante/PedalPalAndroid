package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject

class GetStravaConnectUrlUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(): StravaConnectUrl = bikeRepository.getStravaConnectUrl()
}

