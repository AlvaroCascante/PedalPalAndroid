package com.quetoquenana.and.features.profile.domain.usecase

import com.quetoquenana.and.features.profile.domain.model.Profile
import com.quetoquenana.and.features.profile.domain.model.ProfilePhotoUploadRequest
import com.quetoquenana.and.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UploadProfilePhotoUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(request: ProfilePhotoUploadRequest): Profile {
        return profileRepository.uploadProfilePhoto(request = request)
    }
}

