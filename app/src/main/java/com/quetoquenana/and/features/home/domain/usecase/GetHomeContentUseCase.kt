package com.quetoquenana.and.features.home.domain.usecase

import com.quetoquenana.and.features.announcements.domain.repository.AnnouncementRepository
import com.quetoquenana.and.features.appointments.domain.model.isUpcoming
import com.quetoquenana.and.features.appointments.domain.model.scheduledAtMillis
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentRepository
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import com.quetoquenana.and.features.bikes.domain.model.isActive
import com.quetoquenana.and.features.home.domain.model.HomeContent
import com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetHomeContentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val suggestionRepository: SuggestionsRepository,
    private val announcementRepository: AnnouncementRepository,
    private val bikeRepository: BikeRepository

) {
    suspend operator fun invoke(): HomeContent = coroutineScope {
        val suggestionsDeferred = async {
            loadOrDefault(defaultValue = emptyList()) {
                suggestionRepository.getSuggestions()
            }
        }
        val announcementsDeferred = async {
            loadOrDefault(defaultValue = emptyList()) {
                announcementRepository.getAnnouncements()
            }
        }

        val hasActiveBikesLocally = loadOrDefault(defaultValue = false) {
            bikeRepository.hasActiveBikesLocally()
        }

        val bikes = loadOrDefault(defaultValue = emptyList()) {
            bikeRepository.getBikes(refresh = !hasActiveBikesLocally)
        }.filter(Bike::isActive)
        val appointments = if (bikes.isNotEmpty()) {
            loadOrDefault(defaultValue = emptyList()) {
                appointmentRepository.getAppointments()
                    .filter { appointment -> appointment.isUpcoming() }
                    .sortedBy { appointment -> appointment.scheduledAtMillis ?: Long.MAX_VALUE }
            }
        } else {
            emptyList()
        }

        HomeContent(
            appointments = appointments,
            suggestions = suggestionsDeferred.await(),
            announcements = announcementsDeferred.await(),
            bikes = bikes
        )
    }

    private suspend fun <T> loadOrDefault(defaultValue: T, block: suspend () -> T): T {
        return try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            defaultValue
        }
    }
}