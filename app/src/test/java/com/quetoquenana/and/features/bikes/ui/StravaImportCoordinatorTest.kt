package com.quetoquenana.and.features.bikes.ui

import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StravaImportCoordinatorTest {

    @Test
    fun test_startConnectionIfNeeded_whenConnected_loadsBikes() = runTest {
        val bike = StravaBike(id = "b1", name = "MyBike", nickname = null, primary = false, retired = false, distance = 123.0)

        val coordinator = StravaImportCoordinator(
            scope = this,
            getStravaConnectUrl = { throw AssertionError("connect url should not be requested when already connected") },
            getStravaConnectionStatus = { StravaConnectionStatus(connected = true, status = "CONNECTED", athleteId = null, scope = null) },
            getStravaBikes = { listOf(bike) }
        )

        val events = mutableListOf<StravaImportCoordinator.Event>()
        val job = launch { coordinator.events.collect { events.add(it) } }

        coordinator.startConnectionIfNeeded()
        testScheduler.advanceUntilIdle()

        // Expect a BikeImported event
        assertTrue(events.any { it is StravaImportCoordinator.Event.BikeImported })
        val bikeEvent = events.filterIsInstance<StravaImportCoordinator.Event.BikeImported>().first()
        assertEquals(bike.id, bikeEvent.bike.id)

        job.cancel()
    }

    @Test
    fun test_startConnectionIfNeeded_whenDisconnected_opensBrowser() = runTest {
        val connectUrl = StravaConnectUrl(url = "https://strava.test/connect", state = "s")

        val coordinator = StravaImportCoordinator(
            scope = this,
            getStravaConnectUrl = { connectUrl },
            getStravaConnectionStatus = { StravaConnectionStatus(connected = false, status = "DISCONNECTED", athleteId = null, scope = null) },
            getStravaBikes = { emptyList() }
        )

        val events = mutableListOf<StravaImportCoordinator.Event>()
        val job = launch { coordinator.events.collect { events.add(it) } }

        coordinator.startConnectionIfNeeded()
        testScheduler.advanceUntilIdle()

        // Expect an OpenBrowser event
        assertTrue(events.any { it is StravaImportCoordinator.Event.OpenBrowser })
        val open = events.filterIsInstance<StravaImportCoordinator.Event.OpenBrowser>().first()
        assertEquals(connectUrl.url, open.url)

        // UI should reflect waiting for authorization
        assertTrue(coordinator.uiState.value.isWaitingForAuthorization)

        job.cancel()
    }

    @Test
    fun test_connectToStrava_whenStatusConnected_loadsBikes() = runTest {
        val bike = StravaBike(id = "b2", name = "B2", nickname = null, primary = false, retired = false, distance = 10.0)

        var connectUrlCalled = false

        val coordinator = StravaImportCoordinator(
            scope = this,
            getStravaConnectUrl = { connectUrlCalled = true; StravaConnectUrl(url = "x", state = "s") },
            getStravaConnectionStatus = { StravaConnectionStatus(connected = true, status = "CONNECTED", athleteId = null, scope = null) },
            getStravaBikes = { listOf(bike) }
        )

        val events = mutableListOf<StravaImportCoordinator.Event>()
        val job = launch { coordinator.events.collect { events.add(it) } }

        coordinator.connectToStrava()
        testScheduler.advanceUntilIdle()

        // Should have loaded bikes and emitted BikeImported; connectUrl should not be called
        assertTrue(events.any { it is StravaImportCoordinator.Event.BikeImported })
        assertEquals(false, connectUrlCalled)

        job.cancel()
    }

    @Test
    fun test_connectToStrava_whenStatusCheckThrows_startAuth() = runTest {
        val connectUrl = StravaConnectUrl(url = "https://strava.test/connect2", state = "s2")

        val coordinator = StravaImportCoordinator(
            scope = this,
            getStravaConnectUrl = { connectUrl },
            getStravaConnectionStatus = { throw RuntimeException("network") },
            getStravaBikes = { emptyList() }
        )

        val events = mutableListOf<StravaImportCoordinator.Event>()
        val job = launch { coordinator.events.collect { events.add(it) } }

        coordinator.connectToStrava()
        testScheduler.advanceUntilIdle()

        assertTrue(events.any { it is StravaImportCoordinator.Event.OpenBrowser })
        val open = events.filterIsInstance<StravaImportCoordinator.Event.OpenBrowser>().first()
        assertEquals(connectUrl.url, open.url)

        job.cancel()
    }

    @Test
    fun test_loadStravaBikes_whenGetBikesThrows_andNotConnected_error() = runTest {
        val coordinator = StravaImportCoordinator(
            scope = this,
            getStravaConnectUrl = { StravaConnectUrl(url = "x", state = "s") },
            getStravaConnectionStatus = { StravaConnectionStatus(connected = false, status = "DISCONNECTED", athleteId = null, scope = null) },
            getStravaBikes = { throw RuntimeException("bikes-failed") }
        )

        val events = mutableListOf<StravaImportCoordinator.Event>()
        val job = launch { coordinator.events.collect { events.add(it) } }

        coordinator.loadStravaBikes()
        testScheduler.advanceUntilIdle()

        // Expect ShowError with authorization pending or inability to load bikes
        assertTrue(events.any { it is StravaImportCoordinator.Event.ShowError })
        val err = events.filterIsInstance<StravaImportCoordinator.Event.ShowError>().first()
        assertTrue(err.message.contains("Strava authorization") || err.message.contains("Unable to load Strava"))

        // Ensure loading flag cleared
        assertEquals(false, coordinator.uiState.value.isLoadingBikes)

        job.cancel()
    }
}

