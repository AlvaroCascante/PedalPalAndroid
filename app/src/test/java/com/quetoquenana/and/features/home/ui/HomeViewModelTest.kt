package com.quetoquenana.and.features.home.ui

import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.home.domain.model.HomeContent
import com.quetoquenana.and.features.home.domain.usecase.GetHomeContentUseCase
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when content has bikes it exposes content header section`() = runTest(dispatcher) {
        val content = homeContent(
            bikes = listOf(bike(active = true)),
            appointments = listOf(
                appointment(
                    scheduledAt = "2099-07-08T10:00:00Z",
                    status = "CONFIRMED"
                )
            ),
            suggestions = listOf(suggestion()),
            announcements = listOf(announcement())
        )
        val viewModel = HomeViewModel(
            GetHomeContentUseCase(
                appointmentRepository = FakeAppointmentRepository(content.appointments),
                suggestionRepository = FakeSuggestionsRepository(content.suggestions),
                announcementRepository = FakeAnnouncementRepository(content.announcements),
                bikeRepository = FakeBikeRepository(content.bikes)
            )
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(HeaderSection.Content(appointments = content.appointments), state.headerSection)
        assertEquals(content.bikes, state.bikes)
        assertEquals(content.suggestions, state.suggestions)
        assertEquals(content.announcements, state.announcements)
    }

    @Test
    fun `when content has no bikes it exposes empty header section`() = runTest(dispatcher) {
        val content = homeContent(bikes = emptyList())
        val viewModel = HomeViewModel(
            GetHomeContentUseCase(
                appointmentRepository = FakeAppointmentRepository(content.appointments),
                suggestionRepository = FakeSuggestionsRepository(content.suggestions),
                announcementRepository = FakeAnnouncementRepository(content.announcements),
                bikeRepository = FakeBikeRepository(content.bikes)
            )
        )

        advanceUntilIdle()

        assertEquals(HeaderSection.NoBikes(), viewModel.uiState.value.headerSection)
    }

    private class FakeAppointmentRepository(
        private val appointments: List<Appointment>
    ) : com.quetoquenana.and.features.appointments.domain.repository.AppointmentRepository {
        override suspend fun getAppointments(): List<Appointment> = appointments
        override fun observeAppointments() = kotlinx.coroutines.flow.flowOf(appointments)
        override suspend fun getAppointmentDetail(id: UUID): Appointment = appointments.first { it.id == id }
        override suspend fun createAppointment(request: com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest): Appointment = appointments.first()
    }

    private class FakeSuggestionsRepository(
        private val suggestions: List<Suggestion>
    ) : com.quetoquenana.and.features.suggestions.domain.repository.SuggestionsRepository {
        override suspend fun getSuggestions(): List<Suggestion> = suggestions
    }

    private class FakeAnnouncementRepository(
        private val announcements: List<Announcement>
    ) : com.quetoquenana.and.features.announcements.domain.repository.AnnouncementRepository {
        override suspend fun getAnnouncements(): List<Announcement> = announcements
    }

    private class FakeBikeRepository(
        private val bikes: List<Bike>
    ) : com.quetoquenana.and.features.bikes.domain.repository.BikeRepository {
        override suspend fun getBikeComponentTypes() = emptyList<com.quetoquenana.and.features.bikes.domain.model.ComponentType>()
        override fun observeBikes() = kotlinx.coroutines.flow.flowOf(bikes)
        override suspend fun hasActiveBikesLocally(): Boolean = bikes.any { it.status.equals("ACTIVE", ignoreCase = true) }
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
        override suspend fun getStravaConnectUrl() = com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl("", "")
        override suspend fun getStravaConnectionStatus() = com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus(false, "", null, null)
        override suspend fun getStravaBikes() = emptyList<com.quetoquenana.and.features.bikes.domain.model.StravaBike>()
    }

    private fun homeContent(
        bikes: List<Bike> = emptyList(),
        appointments: List<Appointment> = emptyList(),
        suggestions: List<Suggestion> = emptyList(),
        announcements: List<Announcement> = emptyList()
    ) = HomeContent(
        appointments = appointments,
        suggestions = suggestions,
        announcements = announcements,
        bikes = bikes
    )

    private fun bike(active: Boolean): Bike = Bike(
        id = UUID.randomUUID(),
        name = "Bike",
        type = "ROAD",
        status = if (active) "ACTIVE" else "RETIRED",
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

    private fun appointment(
        scheduledAt: String? = null,
        status: String? = "CONFIRMED"
    ): Appointment = Appointment(
        id = UUID.randomUUID(),
        dateText = "date",
        bikeId = UUID.randomUUID(),
        bikeName = "Bike",
        scheduledAt = scheduledAt,
        status = status
    )

    private fun suggestion(): Suggestion = Suggestion(
        id = UUID.randomUUID(),
        title = "Suggestion",
        subtitle = "Subtitle"
    )

    private fun announcement(): Announcement = Announcement(
        id = UUID.randomUUID(),
        title = "Announcement",
        description = "Description"
    )
}
