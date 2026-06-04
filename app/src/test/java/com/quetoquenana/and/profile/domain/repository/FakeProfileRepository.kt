package com.quetoquenana.and.profile.domain.repository

import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.profile.domain.model.Profile
import com.quetoquenana.and.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class FakeProfileRepository(
    private val profile: Profile? = null,
    private val getProfileException: Throwable? = null,
    private val uploadProfilePhotoResult: Profile? = profile,
    private val uploadProfilePhotoException: Throwable? = null,
) : ProfileRepository {
    private val profileState = MutableStateFlow(profile)
    var getProfileCalled = false
    var getProfileCallCount = 0
    var uploadProfilePhotoCalledWith: MediaUploadRequest? = null

    override fun getCurrentUserProfile(): Flow<Profile> {
        getProfileCalled = true
        getProfileCallCount += 1
        getProfileException?.let { throw it }
        return profileState.filterNotNull()
    }

    override suspend fun uploadProfilePhoto(request: MediaUploadRequest) {
        uploadProfilePhotoCalledWith = request
        uploadProfilePhotoException?.let { throw it }
        uploadProfilePhotoResult?.let { profileState.value = it }
    }
}

