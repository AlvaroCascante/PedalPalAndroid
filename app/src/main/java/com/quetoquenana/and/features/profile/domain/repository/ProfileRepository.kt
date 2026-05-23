package com.quetoquenana.and.features.profile.domain.repository

import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getCurrentUserProfile(): Flow<Profile>
    suspend fun uploadProfilePhoto(request: MediaUploadRequest): Unit
}

