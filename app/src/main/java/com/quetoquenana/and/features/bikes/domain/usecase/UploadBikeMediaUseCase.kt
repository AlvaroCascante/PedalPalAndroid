package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import javax.inject.Inject

class UploadBikeMediaUseCase @Inject constructor(
    private val bikeRepository: BikeRepository
) {
    suspend operator fun invoke(bikeId: String, uploads: List<MediaUploadRequest>) {
        bikeRepository.uploadBikeMedia(bikeId = bikeId, uploads = uploads)
    }
}
