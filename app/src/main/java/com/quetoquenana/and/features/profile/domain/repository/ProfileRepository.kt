package com.quetoquenana.and.features.profile.domain.repository

import com.quetoquenana.and.features.profile.domain.model.Profile
import com.quetoquenana.and.features.profile.domain.model.ProfilePhotoUploadRequest

interface ProfileRepository {
    suspend fun getProfile(): Profile
    suspend fun uploadProfilePhoto(request: ProfilePhotoUploadRequest): Profile
}

