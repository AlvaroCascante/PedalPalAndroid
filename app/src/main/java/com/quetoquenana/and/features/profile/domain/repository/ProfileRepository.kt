package com.quetoquenana.and.features.profile.domain.repository

import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.profile.domain.model.Profile

interface ProfileRepository {
    suspend fun getCurrentUserProfile(): Profile
    suspend fun uploadProfilePhoto(request: MediaUploadRequest)
}

