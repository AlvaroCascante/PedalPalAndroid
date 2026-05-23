package com.quetoquenana.and.features.appointments.domain.usecase

import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.repository.MediaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAppointmentMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
) {
    operator fun invoke(id: String, refresh: Boolean = true): Flow<List<MediaAsset>> {
        return mediaRepository.observeMedia(
            referenceId = id,
            referenceType = MediaReferenceType.APPOINTMENT_DEPOSIT,
            refresh = refresh,
        )
    }
}



