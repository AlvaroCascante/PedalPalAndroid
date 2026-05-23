package com.quetoquenana.and.features.appointments.domain.usecase

import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.domain.repository.MediaRepository
import javax.inject.Inject

class UploadAppointmentMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
) {
    suspend operator fun invoke(id: String, uploads: List<MediaUploadRequest>) {
        mediaRepository.uploadMedia(
            referenceId = id,
            referenceType = MediaReferenceType.APPOINTMENT_DEPOSIT,
            uploads = uploads,
        )
    }
}

