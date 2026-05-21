package com.quetoquenana.and.home.domain.usecase

import com.quetoquenana.and.appointments.domain.repository.FakeAppointmentsRepository
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
import org.junit.Test

class GetHomeContentUseCaseTest {

    @Test
    fun `invoke returns only upcoming appointments ordered by scheduled time`() = runTest {
        val useCase = GetHomeContentUseCase(
            appointmentRepository = FakeAppointmentsRepository(
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
            ),
            suggestionRepository = FakeSuggestionsRepository(),
            announcementRepository = FakeAnnouncementRepository(),
            bikeRepository = FakeBikeRepository(initialBikes = listOf(sampleBike()))
        )

        val result = useCase()

        assertEquals(
            listOf("future-earlier", "future-later"),
            result.appointments.map { it.id }
        )
    }

    private class FakeSuggestionsRepository : SuggestionsRepository {
        override suspend fun getSuggestions(): List<Suggestion> = emptyList()
    }

    private class FakeAnnouncementRepository : AnnouncementRepository {
        override suspend fun getAnnouncements(): List<Announcement> = emptyList()
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

    private fun sampleBike(): Bike {
        return Bike(
            id = "bike-1",
            name = "Trek Domane",
            type = "ROAD",
            status = "ACTIVE",
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

