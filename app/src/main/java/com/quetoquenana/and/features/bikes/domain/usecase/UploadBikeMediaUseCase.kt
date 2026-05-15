package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.features.bikes.domain.model.BikeMediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject

class UploadBikeMediaUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(bikeId: String, uploads: List<BikeMediaUploadRequest>) {
        bikeRepository.uploadBikeMedia(bikeId = bikeId, uploads = uploads)
    }
}
