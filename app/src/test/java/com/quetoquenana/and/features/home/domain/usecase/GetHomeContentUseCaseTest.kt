package com.quetoquenana.and.features.home.domain.usecase

import com.quetoquenana.and.features.home.domain.fakes.FakeAppointmentRepository
import com.quetoquenana.and.features.home.domain.fakes.FakeSuggestionsRepository
import com.quetoquenana.and.features.home.domain.fakes.FakeAnnouncementRepository
import com.quetoquenana.and.features.home.domain.fakes.FakeBikeRepository
import com.quetoquenana.and.features.home.domain.testdata.testBike
import com.quetoquenana.and.features.home.domain.testdata.testAppointment
import com.quetoquenana.and.features.home.domain.testdata.testSuggestion
import com.quetoquenana.and.features.home.domain.testdata.testAnnouncement
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import java.time.Instant

class GetHomeContentUseCaseTest {

    @Test
    fun `when there are active bikes it loads upcoming appointments sorted by scheduled time`() = runTest {
        val firstBike = testBike(id = UUID.randomUUID(), isActive = true)
        val secondBike = testBike(id = UUID.randomUUID(), isActive = false)
        val now = System.currentTimeMillis()
        val laterIso = Instant.ofEpochMilli(now + 2 * 60 * 60 * 1000).toString() // now + 2h
        val earlierIso = Instant.ofEpochMilli(now + 1 * 60 * 60 * 1000).toString() // now + 1h

        val firstAppointment = testAppointment(
            id = UUID.randomUUID(),
            scheduledAt = laterIso,
            dateText = "later"
        )
        val secondAppointment = testAppointment(
            id = UUID.randomUUID(),
            scheduledAt = earlierIso,
            dateText = "earlier"
        )

        val useCase = GetHomeContentUseCase(
            appointmentRepository = FakeAppointmentRepository(listOf(firstAppointment, secondAppointment)),
            suggestionRepository = FakeSuggestionsRepository(listOf(testSuggestion())),
            announcementRepository = FakeAnnouncementRepository(listOf(testAnnouncement())),
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
            appointmentRepository = FakeAppointmentRepository(listOf(testAppointment())),
            suggestionRepository = FakeSuggestionsRepository(listOf(testSuggestion())),
            announcementRepository = FakeAnnouncementRepository(listOf(testAnnouncement())),
            bikeRepository = FakeBikeRepository(
                hasActiveBikesLocallyResult = false,
                bikes = emptyList()
            )
        )

        val result = useCase()

        assertTrue(result.bikes.isEmpty())
        assertTrue(result.appointments.isEmpty())
    }

    @Test
    fun `when all repositories return empty lists it returns empty content`() = runTest {
        val useCase = GetHomeContentUseCase(
            appointmentRepository = FakeAppointmentRepository(emptyList()),
            suggestionRepository = FakeSuggestionsRepository(emptyList()),
            announcementRepository = FakeAnnouncementRepository(emptyList()),
            bikeRepository = FakeBikeRepository(
                hasActiveBikesLocallyResult = false,
                bikes = emptyList()
            )
        )

        val result = useCase()

        assertTrue(result.bikes.isEmpty())
        assertTrue(result.appointments.isEmpty())
        assertTrue(result.suggestions.isEmpty())
        assertTrue(result.announcements.isEmpty())
    }

    @Test
    fun `when only bikes are available it returns bikes without appointments`() = runTest {
        val bikes = listOf(testBike(isActive = true), testBike(isActive = true))
        val useCase = GetHomeContentUseCase(
            appointmentRepository = FakeAppointmentRepository(emptyList()),
            suggestionRepository = FakeSuggestionsRepository(emptyList()),
            announcementRepository = FakeAnnouncementRepository(emptyList()),
            bikeRepository = FakeBikeRepository(
                hasActiveBikesLocallyResult = true,
                bikes = bikes
            )
        )

        val result = useCase()

        assertEquals(bikes, result.bikes)
        assertTrue(result.appointments.isEmpty())
        assertTrue(result.suggestions.isEmpty())
        assertTrue(result.announcements.isEmpty())
    }

    @Test
    fun `when all bikes are inactive they are filtered out`() = runTest {
        val bikes = listOf(testBike(isActive = false), testBike(isActive = false))
        val useCase = GetHomeContentUseCase(
            appointmentRepository = FakeAppointmentRepository(emptyList()),
            suggestionRepository = FakeSuggestionsRepository(emptyList()),
            announcementRepository = FakeAnnouncementRepository(emptyList()),
            bikeRepository = FakeBikeRepository(
                hasActiveBikesLocallyResult = false,
                bikes = bikes
            )
        )

        val result = useCase()

        assertTrue(result.bikes.isEmpty())
    }

    @Test
    fun `when appointments are in the past they are filtered out`() = runTest {
        val activeBike = testBike(isActive = true)
        val now = System.currentTimeMillis()
        val pastTime = Instant.ofEpochMilli(now - 1 * 60 * 60 * 1000).toString() // 1h ago
        val futureTime = Instant.ofEpochMilli(now + 1 * 60 * 60 * 1000).toString() // 1h from now

        val pastAppointment = testAppointment(
            id = UUID.randomUUID(),
            scheduledAt = pastTime,
            dateText = "past"
        )
        val futureAppointment = testAppointment(
            id = UUID.randomUUID(),
            scheduledAt = futureTime,
            dateText = "future"
        )

        val useCase = GetHomeContentUseCase(
            appointmentRepository = FakeAppointmentRepository(
                listOf(pastAppointment, futureAppointment)
            ),
            suggestionRepository = FakeSuggestionsRepository(emptyList()),
            announcementRepository = FakeAnnouncementRepository(emptyList()),
            bikeRepository = FakeBikeRepository(
                hasActiveBikesLocallyResult = true,
                bikes = listOf(activeBike)
            )
        )

        val result = useCase()

        // Only future appointments should be included
        assertEquals(1, result.appointments.size)
        assertEquals(futureAppointment, result.appointments[0])
    }

    @Test
    fun `when multiple appointments have same scheduled time they maintain insertion order`() = runTest {
        val activeBike = testBike(isActive = true)
        val now = System.currentTimeMillis()
        val sameTime = Instant.ofEpochMilli(now + 1 * 60 * 60 * 1000).toString()

        val appointment1 = testAppointment(
            id = UUID.randomUUID(),
            scheduledAt = sameTime,
            dateText = "first"
        )
        val appointment2 = testAppointment(
            id = UUID.randomUUID(),
            scheduledAt = sameTime,
            dateText = "second"
        )

        val useCase = GetHomeContentUseCase(
            appointmentRepository = FakeAppointmentRepository(listOf(appointment1, appointment2)),
            suggestionRepository = FakeSuggestionsRepository(emptyList()),
            announcementRepository = FakeAnnouncementRepository(emptyList()),
            bikeRepository = FakeBikeRepository(
                hasActiveBikesLocallyResult = true,
                bikes = listOf(activeBike)
            )
        )

        val result = useCase()

        // Should maintain insertion order when times are equal
        assertEquals(listOf(appointment1, appointment2), result.appointments)
    }

    @Test
    fun `when there are many bikes filtering works correctly`() = runTest {
        val activeBikes = (1..10).map { testBike(id = UUID.randomUUID(), isActive = true) }
        val inactiveBikes = (1..10).map { testBike(id = UUID.randomUUID(), isActive = false) }
        val allBikes = activeBikes + inactiveBikes

        val useCase = GetHomeContentUseCase(
            appointmentRepository = FakeAppointmentRepository(emptyList()),
            suggestionRepository = FakeSuggestionsRepository(emptyList()),
            announcementRepository = FakeAnnouncementRepository(emptyList()),
            bikeRepository = FakeBikeRepository(
                hasActiveBikesLocallyResult = true,
                bikes = allBikes
            )
        )

        val result = useCase()

        assertEquals(activeBikes.size, result.bikes.size)
        assertEquals(activeBikes, result.bikes)
    }
}
