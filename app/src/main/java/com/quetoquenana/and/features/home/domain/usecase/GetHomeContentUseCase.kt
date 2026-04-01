package com.quetoquenana.and.features.home.domain.usecase

import com.quetoquenana.and.features.announcements.domain.repository.AnnouncementRepository
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentsRepository
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import com.quetoquenana.and.features.home.domain.model.HomeContent
import com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository
import javax.inject.Inject

class GetHomeContentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentsRepository,
    private val suggestionRepository: SuggestionsRepository,
    private val announcementRepository: AnnouncementRepository,
    private val bikeRepository: BikeRepository

) {
    suspend operator fun invoke(): HomeContent {
        val appointments = appointmentRepository.getAppointments()
        val suggestions = suggestionRepository.getSuggestions()
        val announcements = announcementRepository.getAnnouncements()
        val bikes = bikeRepository.getBikes()
        return HomeContent(
            appointments = appointments,
            suggestions = suggestions,
            announcements = announcements,
            bikes = bikes
        )
    }
}