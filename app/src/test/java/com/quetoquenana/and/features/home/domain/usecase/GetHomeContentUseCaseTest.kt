package com.quetoquenana.and.features.home.domain.usecase

import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.repository.AnnouncementRepository
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.repository.AppointmentRepository
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import com.quetoquenana.and.features.home.domain.model.HomeContent
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class GetHomeContentUseCaseTest {

    @Test
    fun `when there are active bikes it loads upcoming appointments sorted by scheduled time`() = runTest {
        val firstBike = bike(id = UUID.randomUUID(), isActive = true)
        val secondBike = bike(id = UUID.randomUUID(), isActive = false)
        val firstAppointment = appointment(
            id = UUID.randomUUID(),
            scheduledAt = "2099-07-08T10:00:00Z",
            dateText = "later"
        )
        val secondAppointment = appointment(
            id = UUID.randomUUID(),
            scheduledAt = "2099-07-08T09:00:00Z",
            dateText = "earlier"
        )

        val useCase = GetHomeContentUseCase(
            appointmentRepository = FakeAppointmentRepository(listOf(firstAppointment, secondAppointment)),
            suggestionRepository = FakeSuggestionsRepository(listOf(suggestion())),
            announcementRepository = FakeAnnouncementRepository(listOf(announcement())),
            bikeRepository = FakeBikeRepository(
                hasActiveBikesLocallyResult = true,
                bikes = listOf(firstBike, secondBike)
            )
        )

        val result = useCase()

        assertEquals(listOf(firstBike), result.bikes)
        assertEquals(listOf(secondAppointment, firstAppointment), result.appointments)
        assertEquals(1, result.suggestions.size)
        assertEquals(1, result.announcements.size)
    }

    @Test
    fun `when there are no active bikes it skips appointments`() = runTest {
        val useCase = GetHomeContentUseCase(
            appointmentRepository = FakeAppointmentRepository(listOf(appointment())),
            suggestionRepository = FakeSuggestionsRepository(listOf(suggestion())),
            announcementRepository = FakeAnnouncementRepository(listOf(announcement())),
            bikeRepository = FakeBikeRepository(
                hasActiveBikesLocallyResult = false,
                bikes = emptyList()
            )
        )

        val result = useCase()

        assertTrue(result.bikes.isEmpty())
        assertTrue(result.appointments.isEmpty())
    }

    private class FakeAppointmentRepository(
        private val appointments: List<Appointment>
    ) : AppointmentRepository {
        override suspend fun getAppointments(): List<Appointment> = appointments
        override fun observeAppointments(): Flow<List<Appointment>> = flowOf(appointments)
        override suspend fun getAppointmentDetail(id: UUID): Appointment = appointments.first { it.id == id }
        override suspend fun createAppointment(request: com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest): Appointment = appointments.first()
    }

    private class FakeSuggestionsRepository(
        private val suggestions: List<Suggestion>
    ) : SuggestionsRepository {
        override suspend fun getSuggestions(): List<Suggestion> = suggestions
    }

    private class FakeAnnouncementRepository(
        private val announcements: List<Announcement>
    ) : AnnouncementRepository {
        override suspend fun getAnnouncements(): List<Announcement> = announcements
    }

    private class FakeBikeRepository(
        private val hasActiveBikesLocallyResult: Boolean,
        private val bikes: List<Bike>
    ) : BikeRepository {
        override suspend fun getBikeComponentTypes() = emptyList<com.quetoquenana.and.features.bikes.domain.model.ComponentType>()
        override fun observeBikes(): Flow<List<Bike>> = flowOf(bikes)
        override suspend fun hasActiveBikesLocally(): Boolean = hasActiveBikesLocallyResult
        override suspend fun getBikeProfileImageUrl(id: UUID): String? = null
        override suspend fun getBikes(refresh: Boolean): List<Bike> = bikes
        override suspend fun getBike(id: UUID): Bike = bikes.first { it.id == id }
        override suspend fun getBikeHistory(id: UUID) = emptyList<com.quetoquenana.and.features.bikes.domain.model.BikeHistory>()
        override suspend fun getBikeMedia(id: UUID) = emptyList<com.quetoquenana.and.features.bikes.domain.model.BikeMedia>()
        override suspend fun uploadBikeMedia(bikeId: UUID, uploads: List<com.quetoquenana.and.core.media.domain.model.MediaUploadRequest>) = Unit
        override suspend fun uploadBikeProfileImage(bikeId: UUID, upload: com.quetoquenana.and.core.media.domain.model.MediaUploadRequest) = Unit
        override suspend fun createBike(request: com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest) = Unit
        override suspend fun addBikeComponent(bikeId: UUID, request: com.quetoquenana.and.features.bikes.domain.model.AddComponentRequest) = com.quetoquenana.and.features.bikes.domain.model.Component(
            id = UUID.randomUUID(),
            type = "CHAIN",
            name = "component",
            status = "ACTIVE",
            brand = null,
            model = null,
            notes = null,
            odometerKm = 0,
            usageTimeMinutes = 0
        )
        override suspend fun getStravaConnectUrl() = com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl(
            url = "",
            state = ""
        )
        override suspend fun getStravaConnectionStatus() = com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus(
            connected = false,
            status = "",
            athleteId = null,
            scope = null
        )
        override suspend fun getStravaBikes() = emptyList<com.quetoquenana.and.features.bikes.domain.model.StravaBike>()
    }

    private fun bike(id: UUID = UUID.randomUUID(), isActive: Boolean): Bike {
        return Bike(
            id = id,
            name = "Bike",
            type = "ROAD",
            status = if (isActive) "ACTIVE" else "RETIRED",
            isPublic = true,
            isExternalSync = false,
            brand = null,
            model = null,
            year = null,
            serialNumber = null,
            notes = null,
            odometerKm = 0.0,
            usageTimeMinutes = 0,
            externalGearId = null,
            externalSyncProvider = ""
        )
    }

    private fun appointment(
        id: UUID = UUID.randomUUID(),
        scheduledAt: String? = null,
        dateText: String = "date"
    ): Appointment {
        return Appointment(
            id = id,
            dateText = dateText,
            bikeId = UUID.randomUUID(),
            bikeName = "Bike",
            storeLocationId = null,
            storeLocationName = null,
            currency = null,
            scheduledAt = scheduledAt,
            status = "CONFIRMED",
            notes = null,
            deposit = null,
            requestedServices = emptyList()
        )
    }

    private fun suggestion(): Suggestion = Suggestion(
        id = UUID.randomUUID(),
        title = "Suggestion",
        subtitle = "Subtitle"
    )

    private fun announcement(): Announcement = Announcement(
        id = UUID.randomUUID(),
        title = "Announcement",
        description = "Description",
        url = null,
        media = emptyList()
    )
}
