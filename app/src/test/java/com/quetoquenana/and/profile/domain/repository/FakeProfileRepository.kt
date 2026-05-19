package com.quetoquenana.and.profile.domain.repository

import com.quetoquenana.and.features.profile.domain.model.Profile
import com.quetoquenana.and.features.profile.domain.model.ProfilePhotoUploadRequest
import com.quetoquenana.and.features.profile.domain.repository.ProfileRepository

class FakeProfileRepository(
    private val profile: Profile? = null,
    private val getProfileException: Throwable? = null,
    private val uploadProfilePhotoResult: Profile? = profile,
    private val uploadProfilePhotoException: Throwable? = null,
) : ProfileRepository {
    var getProfileCalled = false
    var uploadProfilePhotoCalledWith: ProfilePhotoUploadRequest? = null

    override suspend fun getProfile(): Profile {
        getProfileCalled = true
        getProfileException?.let { throw it }
        return profile ?: error("No profile configured")
    }

    override suspend fun uploadProfilePhoto(request: ProfilePhotoUploadRequest): Profile {
        uploadProfilePhotoCalledWith = request
        uploadProfilePhotoException?.let { throw it }
        return uploadProfilePhotoResult ?: error("No uploaded profile configured")
    }
}

