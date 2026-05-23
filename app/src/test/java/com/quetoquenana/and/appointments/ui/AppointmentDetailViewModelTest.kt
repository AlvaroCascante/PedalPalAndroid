package com.quetoquenana.and.appointments.ui

import androidx.lifecycle.SavedStateHandle
import com.quetoquenana.and.appointments.domain.repository.FakeAppointmentRepository
import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.core.media.domain.repository.MediaRepository
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.usecase.GetAppointmentDetailUseCase
import com.quetoquenana.and.features.appointments.domain.usecase.ObserveAppointmentMediaUseCase
import com.quetoquenana.and.features.appointments.domain.usecase.UploadAppointmentMediaUseCase
import com.quetoquenana.and.features.appointments.ui.AppointmentDetailEvent
import com.quetoquenana.and.features.appointments.ui.AppointmentDetailUiState
import com.quetoquenana.and.features.appointments.ui.AppointmentDetailViewModel
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import com.quetoquenana.and.features.stores.domain.repository.StoreRepository
import com.quetoquenana.and.features.stores.domain.usecase.GetStoresUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppointmentDetailViewModelTest {

    private val initialAttachment = MediaAsset(
        referenceId = "appointment-1",
        referenceType = MediaReferenceType.APPOINTMENT_DEPOSIT,
        mediaId = "media-1",
        url = "https://example.com/media-1.png",
        contentType = "image/png",
        name = MediaReferenceType.APPOINTMENT_DEPOSIT.mediaName,
        altText = "Deposit receipt",
        isPrivate = true,
        urlExpireAt = null,
        updatedAt = 0L,
        fetchedAt = 0L,
    )

    @Test
    fun `load resolves location name from stores`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentRepository(
                appointments = listOf(
                    appointment(
                        id = "appointment-1",
                        storeLocationId = "location-1"
                    )
                )
            )
            val storeRepository = FakeStoreRepository(
                cachedStores = listOf(storeWithLocation(id = "location-1", name = "San Jose Workshop"))
            )
            val mediaRepository = FakeMediaRepository(
                initialMedia = listOf(initialAttachment)
            )

            val viewModel = AppointmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("id" to "appointment-1")),
                getAppointmentDetail = GetAppointmentDetailUseCase(appointmentsRepository),
                getStores = GetStoresUseCase(storeRepository),
                observeAppointmentMedia = ObserveAppointmentMediaUseCase(mediaRepository),
                uploadAppointmentMedia = UploadAppointmentMediaUseCase(mediaRepository),
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value as AppointmentDetailUiState.Content
            assertEquals("San Jose Workshop", state.appointment.storeLocationName)
            assertEquals("CRC", state.appointment.currency)
            assertEquals(listOf(initialAttachment), state.attachments)
            assertEquals(listOf(false), storeRepository.refreshRequests)
            assertEquals(
                listOf("appointment-1" to MediaReferenceType.APPOINTMENT_DEPOSIT),
                mediaRepository.observedReferences
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `load refreshes stores when cached location is missing`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentRepository(
                appointments = listOf(
                    appointment(
                        id = "appointment-2",
                        storeLocationId = "location-2"
                    )
                )
            )
            val storeRepository = FakeStoreRepository(
                cachedStores = listOf(storeWithLocation(id = "location-1", name = "San Jose Workshop")),
                refreshedStores = listOf(storeWithLocation(id = "location-2", name = "Escazu Workshop"))
            )
            val mediaRepository = FakeMediaRepository()

            val viewModel = AppointmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("id" to "appointment-2")),
                getAppointmentDetail = GetAppointmentDetailUseCase(appointmentsRepository),
                getStores = GetStoresUseCase(storeRepository),
                observeAppointmentMedia = ObserveAppointmentMediaUseCase(mediaRepository),
                uploadAppointmentMedia = UploadAppointmentMediaUseCase(mediaRepository),
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value as AppointmentDetailUiState.Content
            assertEquals("Escazu Workshop", state.appointment.storeLocationName)
            assertEquals("CRC", state.appointment.currency)
            assertEquals(listOf(false, true), storeRepository.refreshRequests)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `load keeps appointment content when stores lookup fails`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentRepository(
                appointments = listOf(
                    appointment(
                        id = "appointment-3",
                        storeLocationId = "location-3"
                    )
                )
            )
            val storeRepository = object : StoreRepository {
                override suspend fun getStores(refresh: Boolean): List<Store> {
                    throw IllegalStateException("Stores unavailable")
                }
            }
            val mediaRepository = FakeMediaRepository()

            val viewModel = AppointmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("id" to "appointment-3")),
                getAppointmentDetail = GetAppointmentDetailUseCase(appointmentsRepository),
                getStores = GetStoresUseCase(storeRepository),
                observeAppointmentMedia = ObserveAppointmentMediaUseCase(mediaRepository),
                uploadAppointmentMedia = UploadAppointmentMediaUseCase(mediaRepository),
            )

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is AppointmentDetailUiState.Content)
            state as AppointmentDetailUiState.Content
            assertEquals(null, state.appointment.storeLocationName)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onPaymentProofsSelected uploads attachments and emits success event`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentRepository(
                appointments = listOf(appointment(id = "appointment-4", storeLocationId = "location-1"))
            )
            val storeRepository = FakeStoreRepository(
                cachedStores = listOf(storeWithLocation(id = "location-1", name = "San Jose Workshop"))
            )
            val mediaRepository = FakeMediaRepository()
            val viewModel = AppointmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("id" to "appointment-4")),
                getAppointmentDetail = GetAppointmentDetailUseCase(appointmentsRepository),
                getStores = GetStoresUseCase(storeRepository),
                observeAppointmentMedia = ObserveAppointmentMediaUseCase(mediaRepository),
                uploadAppointmentMedia = UploadAppointmentMediaUseCase(mediaRepository),
            )
            advanceUntilIdle()

            val eventDeferred = CompletableDeferred<AppointmentDetailEvent>()
            val job = launch {
                viewModel.events.collect { event ->
                    if (!eventDeferred.isCompleted) eventDeferred.complete(event)
                }
            }

            val upload = MediaUploadRequest(
                name = MediaReferenceType.APPOINTMENT_DEPOSIT.mediaName,
                altText = "Deposit receipt",
                contentType = "image/png",
                bytes = byteArrayOf(1, 2, 3),
                isPublic = true
            )

            viewModel.onPaymentProofsSelected(listOf(upload))
            advanceUntilIdle()

            val state = viewModel.uiState.value as AppointmentDetailUiState.Content
            assertEquals(listOf(upload), mediaRepository.uploadMediaCalledWith)
            assertEquals(1, state.attachments.size)
            assertEquals(MediaReferenceType.APPOINTMENT_DEPOSIT, state.attachments.first().referenceType)
            assertEquals(
                AppointmentDetailEvent.ShowMessage("Payment proof attached"),
                withTimeoutOrNull(1_000) { eventDeferred.await() }
            )
            job.cancel()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onPaymentProofsSelected emits error when upload list is empty`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentRepository(
                appointments = listOf(appointment(id = "appointment-5", storeLocationId = "location-1"))
            )
            val storeRepository = FakeStoreRepository(
                cachedStores = listOf(storeWithLocation(id = "location-1", name = "San Jose Workshop"))
            )
            val mediaRepository = FakeMediaRepository()
            val viewModel = AppointmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("id" to "appointment-5")),
                getAppointmentDetail = GetAppointmentDetailUseCase(appointmentsRepository),
                getStores = GetStoresUseCase(storeRepository),
                observeAppointmentMedia = ObserveAppointmentMediaUseCase(mediaRepository),
                uploadAppointmentMedia = UploadAppointmentMediaUseCase(mediaRepository),
            )
            advanceUntilIdle()

            val eventDeferred = CompletableDeferred<AppointmentDetailEvent>()
            val job = launch {
                viewModel.events.collect { event ->
                    if (!eventDeferred.isCompleted) eventDeferred.complete(event)
                }
            }

            viewModel.onPaymentProofsSelected(emptyList())
            advanceUntilIdle()

            assertEquals(
                AppointmentDetailEvent.ShowError("No valid image selected"),
                withTimeoutOrNull(1_000) { eventDeferred.await() }
            )
            job.cancel()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `onPaymentProofsSelected emits error when upload fails`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val appointmentsRepository = FakeAppointmentRepository(
                appointments = listOf(appointment(id = "appointment-6", storeLocationId = "location-1"))
            )
            val storeRepository = FakeStoreRepository(
                cachedStores = listOf(storeWithLocation(id = "location-1", name = "San Jose Workshop"))
            )
            val mediaRepository = FakeMediaRepository(uploadFailure = IllegalStateException("upload failed"))
            val viewModel = AppointmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("id" to "appointment-6")),
                getAppointmentDetail = GetAppointmentDetailUseCase(appointmentsRepository),
                getStores = GetStoresUseCase(storeRepository),
                observeAppointmentMedia = ObserveAppointmentMediaUseCase(mediaRepository),
                uploadAppointmentMedia = UploadAppointmentMediaUseCase(mediaRepository),
            )
            advanceUntilIdle()

            val eventDeferred = CompletableDeferred<AppointmentDetailEvent>()
            val job = launch {
                viewModel.events.collect { event ->
                    if (!eventDeferred.isCompleted) eventDeferred.complete(event)
                }
            }

            viewModel.onPaymentProofsSelected(
                listOf(
                    MediaUploadRequest(
                        name = MediaReferenceType.APPOINTMENT_DEPOSIT.mediaName,
                        altText = "Deposit receipt",
                        contentType = "image/png",
                        bytes = byteArrayOf(7, 8, 9),
                        isPublic = true
                    )
                )
            )
            advanceUntilIdle()

            assertEquals(
                AppointmentDetailEvent.ShowError("upload failed"),
                withTimeoutOrNull(1_000) { eventDeferred.await() }
            )
            job.cancel()
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun appointment(
        id: String,
        storeLocationId: String
    ): Appointment {
        return Appointment(
            id = id,
            dateText = "2026-05-22",
            bikeId = "bike-1",
            storeLocationId = storeLocationId,
            scheduledAt = "2026-05-22T09:30:00Z",
            status = "CONFIRMED"
        )
    }

    private fun storeWithLocation(id: String, name: String): Store {
        return Store(
            id = "store-1",
            name = "PedalPal",
            locations = listOf(
                StoreLocation(
                    id = id,
                    storeId = "store-1",
                    name = name,
                    storePrefix = null,
                    website = null,
                    address = null,
                    latitude = null,
                    longitude = null,
                    phone = null,
                    currency = "CRC",
                    timezone = null,
                    status = "ACTIVE"
                )
            )
        )
    }

    private class FakeStoreRepository(
        private val cachedStores: List<Store> = emptyList(),
        private val refreshedStores: List<Store> = cachedStores
    ) : StoreRepository {
        val refreshRequests = mutableListOf<Boolean>()

        override suspend fun getStores(refresh: Boolean): List<Store> {
            refreshRequests += refresh
            return if (refresh) refreshedStores else cachedStores
        }
    }

    private class FakeMediaRepository(
        initialMedia: List<MediaAsset> = emptyList(),
        private val uploadFailure: Throwable? = null,
    ) : MediaRepository {
        val observedReferences = mutableListOf<Pair<String, MediaReferenceType>>()
        var uploadMediaCalledWith: List<MediaUploadRequest>? = null

        private val mediaState = MutableStateFlow(initialMedia)

        override fun observeMedia(
            referenceId: String,
            referenceType: MediaReferenceType,
            refresh: Boolean,
        ): Flow<List<MediaAsset>> {
            observedReferences += referenceId to referenceType
            return mediaState.map { media ->
                media.filter { it.referenceId == referenceId && it.referenceType == referenceType }
            }
        }

        override fun observePrimaryMedia(
            referenceId: String,
            referenceType: MediaReferenceType,
            refresh: Boolean,
        ): Flow<MediaAsset?> {
            return observeMedia(
                referenceId = referenceId,
                referenceType = referenceType,
                refresh = refresh,
            ).map { media -> media.firstOrNull() }
        }

        override suspend fun refreshMedia(referenceId: String, referenceType: MediaReferenceType) = Unit

        override suspend fun uploadMedia(
            referenceId: String,
            referenceType: MediaReferenceType,
            uploads: List<MediaUploadRequest>,
        ) {
            uploadFailure?.let { throw it }
            uploadMediaCalledWith = uploads
            val createdMedia = uploads.mapIndexed { index, upload ->
                MediaAsset(
                    referenceId = referenceId,
                    referenceType = referenceType,
                    mediaId = "media-${mediaState.value.size + index + 1}",
                    url = "https://example.com/${upload.name}-${index + 1}.png",
                    contentType = upload.contentType,
                    name = upload.name,
                    altText = upload.altText,
                    isPrivate = !upload.isPublic,
                    urlExpireAt = null,
                    updatedAt = 0L,
                    fetchedAt = 0L,
                )
            }
            mediaState.value = mediaState.value + createdMedia
        }
    }
}

