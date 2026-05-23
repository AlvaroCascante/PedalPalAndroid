package com.quetoquenana.and.home.domain.usecase

import com.quetoquenana.and.appointments.domain.repository.FakeAppointmentRepository
import com.quetoquenana.and.bikes.domain.repository.FakeBikeRepository
import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.repository.AnnouncementRepository
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.home.domain.usecase.GetHomeContentUseCase
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetHomeContentUseCaseTest {

    @Test
    fun `invoke returns only upcoming appointments ordered by scheduled time`() = runTest {
        val appointmentRepository = FakeAppointmentRepository(
            appointments = listOf(
                appointment(
                    id = "future-later",
                    scheduledAt = "2099-06-02T10:00:00Z",
                    status = "CONFIRMED"
                ),
                appointment(
                    id = "past",
                    scheduledAt = "2020-06-01T10:00:00Z",
                    status = "CONFIRMED"
                ),
                appointment(
                    id = "future-earlier",
                    scheduledAt = "2099-06-01T10:00:00Z",
                    status = "REQUESTED"
                ),
                appointment(
                    id = "invalid-date",
                    scheduledAt = "not-a-date",
                    status = "REQUESTED"
                ),
                appointment(
                    id = "closed-future",
                    scheduledAt = "2099-07-01T10:00:00Z",
                    status = "CANCELLED"
                )
            )
        )
        val useCase = GetHomeContentUseCase(
            appointmentRepository = appointmentRepository,
            suggestionRepository = FakeSuggestionsRepository(),
            announcementRepository = FakeAnnouncementRepository(),
            bikeRepository = FakeBikeRepository(initialBikes = listOf(sampleBike()))
        )

        val result = useCase()

        assertEquals(
            listOf("future-earlier", "future-later"),
            result.appointments.map { it.id }
        )
        assertTrue(appointmentRepository.getAppointmentsCalled)
    }

    @Test
    fun `invoke refreshes bikes when no active bikes exist locally and then fetches appointments`() = runTest {
        val appointmentRepository = FakeAppointmentRepository(
            appointments = listOf(
                appointment(
                    id = "future",
                    scheduledAt = "2099-06-01T10:00:00Z",
                    status = "CONFIRMED"
                )
            )
        )
        val bikeRepository = FakeBikeRepository(
            initialBikes = listOf(sampleBike(id = "inactive-bike", status = "INACTIVE")),
            refreshedBikes = listOf(sampleBike(id = "active-bike", status = "ACTIVE")),
            localActiveBikesAvailable = false
        )
        val useCase = GetHomeContentUseCase(
            appointmentRepository = appointmentRepository,
            suggestionRepository = FakeSuggestionsRepository(),
            announcementRepository = FakeAnnouncementRepository(),
            bikeRepository = bikeRepository
        )

        val result = useCase()

        assertEquals(listOf("active-bike"), result.bikes.map { it.id })
        assertEquals(1, bikeRepository.getBikesCallCount)
        assertTrue(appointmentRepository.getAppointmentsCalled)
    }

    @Test
    fun `invoke skips appointments when no active bikes are available`() = runTest {
        val appointmentRepository = FakeAppointmentRepository(
            appointments = listOf(
                appointment(
                    id = "future",
                    scheduledAt = "2099-06-01T10:00:00Z",
                    status = "CONFIRMED"
                )
            )
        )
        val useCase = GetHomeContentUseCase(
            appointmentRepository = appointmentRepository,
            suggestionRepository = FakeSuggestionsRepository(),
            announcementRepository = FakeAnnouncementRepository(),
            bikeRepository = FakeBikeRepository(
                initialBikes = emptyList(),
                refreshedBikes = emptyList(),
                localActiveBikesAvailable = false
            )
        )

        val result = useCase()

        assertTrue(result.bikes.isEmpty())
        assertTrue(result.appointments.isEmpty())
        assertFalse(appointmentRepository.getAppointmentsCalled)
    }

    @Test
    fun `invoke keeps home content available when suggestions fail`() = runTest {
        val appointmentRepository = FakeAppointmentRepository(
            appointments = listOf(
                appointment(
                    id = "future",
                    scheduledAt = "2099-06-01T10:00:00Z",
                    status = "CONFIRMED"
                )
            )
        )
        val announcement = Announcement(
            id = "announcement-1",
            title = "Weekend promo",
            description = "Check the latest offer",
            media = emptyList(),
            subTitle = null,
            url = null
        )
        val useCase = GetHomeContentUseCase(
            appointmentRepository = appointmentRepository,
            suggestionRepository = FailingSuggestionsRepository(),
            announcementRepository = FakeAnnouncementRepository(announcements = listOf(announcement)),
            bikeRepository = FakeBikeRepository(initialBikes = listOf(sampleBike()))
        )

        val result = useCase()

        assertEquals(listOf("future"), result.appointments.map { it.id })
        assertEquals(listOf("announcement-1"), result.announcements.map { it.id })
        assertTrue(result.suggestions.isEmpty())
    }

    private class FakeSuggestionsRepository : SuggestionsRepository {
        override suspend fun getSuggestions(): List<Suggestion> = emptyList()
    }

    private class FailingSuggestionsRepository : SuggestionsRepository {
        override suspend fun getSuggestions(): List<Suggestion> {
            throw IllegalStateException("Suggestions unavailable")
        }
    }

    private class FakeAnnouncementRepository(
        private val announcements: List<Announcement> = emptyList()
    ) : AnnouncementRepository {
        override suspend fun getAnnouncements(): List<Announcement> = announcements
    }

    private fun appointment(
        id: String,
        scheduledAt: String?,
        status: String
    ): Appointment {
        return Appointment(
            id = id,
            dateText = id,
            bikeId = "bike-1",
            scheduledAt = scheduledAt,
            status = status
        )
    }

    private fun sampleBike(
        id: String = "bike-1",
        status: String = "ACTIVE"
    ): Bike {
        return Bike(
            id = id,
            name = "Trek Domane",
            type = "ROAD",
            status = status,
            isPublic = false,
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
}

