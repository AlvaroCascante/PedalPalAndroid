package com.quetoquenana.and.profile.domain.repository

import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.profile.domain.model.Profile
import com.quetoquenana.and.features.profile.domain.repository.ProfileRepository

class FakeProfileRepository(
    private val profile: Profile? = null,
    private val getProfileException: Throwable? = null,
    private val uploadProfilePhotoResult: Profile? = profile,
    private val uploadProfilePhotoException: Throwable? = null,
) : ProfileRepository {
    private var currentProfile: Profile? = profile
    var getProfileCalled = false
    var getProfileCallCount = 0
    var uploadProfilePhotoCalledWith: MediaUploadRequest? = null

    override suspend fun getProfile(): Profile {
        getProfileCalled = true
        getProfileCallCount += 1
        getProfileException?.let { throw it }
        return currentProfile ?: error("No profile configured")
    }

    override suspend fun uploadProfilePhoto(request: MediaUploadRequest): Profile {
        uploadProfilePhotoCalledWith = request
        uploadProfilePhotoException?.let { throw it }
        return (uploadProfilePhotoResult ?: error("No uploaded profile configured")).also {
            currentProfile = it
        }
    }
}

