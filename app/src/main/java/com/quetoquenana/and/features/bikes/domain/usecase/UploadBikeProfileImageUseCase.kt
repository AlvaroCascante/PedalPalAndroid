package com.quetoquenana.and.features.bikes.domain.usecase

import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import java.util.UUID
import javax.inject.Inject

class UploadBikeProfileImageUseCase @Inject constructor(
    private val bikeRepository: BikeRepository,
) {
    suspend operator fun invoke(bikeId: UUID, upload: MediaUploadRequest) {
        bikeRepository.uploadBikeProfileImage(bikeId = bikeId, upload = upload)
    }
}

