package com.quetoquenana.and.features.profile.domain.usecase

import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UploadProfilePhotoUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(request: MediaUploadRequest) {
        profileRepository.uploadProfilePhoto(request = request)
    }
}

