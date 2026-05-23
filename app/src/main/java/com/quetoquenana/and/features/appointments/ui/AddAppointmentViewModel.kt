package com.quetoquenana.and.features.appointments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.appointments.domain.model.AppointmentCreationException
import com.quetoquenana.and.features.appointments.domain.model.CreateAppointmentRequest
import com.quetoquenana.and.features.appointments.domain.model.RequestedServiceItem
import com.quetoquenana.and.features.appointments.domain.model.RequestedServiceType
import com.quetoquenana.and.features.appointments.domain.usecase.CreateAppointmentUseCase
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikesUseCase
import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import com.quetoquenana.and.features.services.domain.model.ServicePackage
import com.quetoquenana.and.features.services.domain.model.ServiceProduct
import com.quetoquenana.and.features.services.domain.usecase.GetServiceCatalogUseCase
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import com.quetoquenana.and.features.stores.domain.usecase.GetStoresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@HiltViewModel
class AddAppointmentViewModel @Inject constructor(
    private val getStoresUseCase: GetStoresUseCase,
    private val getServiceCatalogUseCase: GetServiceCatalogUseCase,
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val getBikesUseCase: GetBikesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAppointmentUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddAppointmentEvent>()
    val events: SharedFlow<AddAppointmentEvent> = _events.asSharedFlow()

    init {
        loadBikes()
        loadStores()
    }

    fun loadBikes(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBikes = true, errorMessage = null) }
            try {
                val bikes = getBikesUseCase(refresh = refresh)
                _uiState.update { current ->
                    val selectedBikeId = current.selectedBikeId
                        ?.takeIf { bikeId -> bikes.any { it.id == bikeId } }
                        ?: bikes.singleOrNull()?.id

                    current.copy(
                        bikes = bikes,
                        selectedBikeId = selectedBikeId,
                        isLoadingBikes = false
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoadingBikes = false,
                        errorMessage = throwable.message ?: "Unable to load bikes"
                    )
                }
            }
        }
    }

    fun loadStores(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStores = true, errorMessage = null) }
            try {
                val stores = getStoresUseCase(refresh = refresh)
                var locationToLoad: String? = null
                _uiState.update { current ->
                    val selectedStoreId = current.selectedStoreId
                        ?.takeIf { storeId -> stores.any { it.id == storeId } }
                        ?: stores.singleOrNull()?.id
                    val selectedStore = stores.firstOrNull { it.id == selectedStoreId }
                    val selectedLocationId = current.selectedLocationId
                        ?.takeIf { locationId ->
                            selectedStore?.locations.orEmpty().any { it.id == locationId }
                        }
                        ?: selectedStore?.locations.orEmpty().singleOrNull()?.id
                    if (selectedLocationId != null && selectedLocationId != current.selectedLocationId) {
                        locationToLoad = selectedLocationId
                    }

                    current.copy(
                        stores = stores,
                        selectedStoreId = selectedStoreId,
                        selectedLocationId = selectedLocationId,
                        isLoadingStores = false
                    )
                }
                locationToLoad?.let { loadServiceCatalog(storeLocationId = it, refresh = true) }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoadingStores = false,
                        errorMessage = throwable.message ?: "Unable to load stores"
                    )
                }
            }
        }
    }

    fun onStoreSelected(storeId: String) {
        _uiState.update { current ->
            current.copy(
                selectedStoreId = storeId,
                selectedLocationId = null,
                serviceCatalog = ServiceCatalog(),
                selectedPackageIds = emptySet(),
                selectedProductIds = emptySet(),
                catalogFetchErrorMessage = null,
                isUsingCachedCatalog = false,
                errorMessage = null
            )
        }
    }

    fun onLocationSelected(locationId: String) {
        _uiState.update {
            it.copy(
                selectedLocationId = locationId,
                serviceCatalog = ServiceCatalog(),
                selectedPackageIds = emptySet(),
                selectedProductIds = emptySet(),
                catalogFetchErrorMessage = null,
                isUsingCachedCatalog = false,
                errorMessage = null
            )
        }
        loadServiceCatalog(storeLocationId = locationId, refresh = true)
    }

    fun onScheduledDateSelected(selectedDateMillis: Long) {
        val todayUtcStartMillis = LocalDate.now(ZoneOffset.UTC)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
        if (selectedDateMillis < todayUtcStartMillis) {
            emitCreateError("Select today or a future date for the appointment.")
            return
        }

        _uiState.update {
            it.copy(
                scheduledAt = Instant.ofEpochMilli(selectedDateMillis).toString(),
                submitErrorMessage = null,
                errorMessage = null
            )
        }
    }

    fun loadServiceCatalog(refresh: Boolean = true) {
        val storeLocationId = _uiState.value.selectedLocationId ?: return
        loadServiceCatalog(storeLocationId = storeLocationId, refresh = refresh)
    }

    private fun loadServiceCatalog(storeLocationId: String, refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingServices = true,
                    isLoadingRemoteCatalog = true,
                    catalogFetchErrorMessage = null,
                    errorMessage = null
                )
            }
            try {
                val catalog = getServiceCatalogUseCase(
                    storeLocationId = storeLocationId,
                    refresh = refresh
                )
                _uiState.update {
                    if (it.selectedLocationId != storeLocationId) return@update it
                    it.copy(
                        serviceCatalog = catalog,
                        selectedPackageIds = it.selectedPackageIds.filter(catalog.packages.map(ServicePackage::id)::contains).toSet(),
                        selectedProductIds = it.selectedProductIds.filter(catalog.products.map(ServiceProduct::id)::contains).toSet(),
                        isLoadingServices = false,
                        isLoadingRemoteCatalog = false,
                        isUsingCachedCatalog = catalog.isFromCache,
                        catalogLastUpdated = catalog.lastUpdated,
                        catalogFetchErrorMessage = catalog.fetchErrorMessage
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoadingServices = false,
                        isLoadingRemoteCatalog = false,
                        catalogFetchErrorMessage = throwable.message ?: "Unable to load services",
                        errorMessage = throwable.message ?: "Unable to load services"
                    )
                }
            }
        }
    }

    fun onPackageToggled(packageId: String) {
        _uiState.update {
            it.copy(selectedPackageIds = it.selectedPackageIds.toggle(packageId))
        }
    }

    fun onProductToggled(productId: String) {
        _uiState.update {
            it.copy(selectedProductIds = it.selectedProductIds.toggle(productId))
        }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onBikeSelected(bikeId: String) {
        _uiState.update { it.copy(selectedBikeId = bikeId, errorMessage = null) }
    }

    fun submitAppointment(request: CreateAppointmentRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, submitErrorMessage = null, errorMessage = null) }
            try {
                createAppointmentUseCase(request)
                _uiState.update { it.copy(isSubmitting = false) }
                _events.emit(AddAppointmentEvent.AppointmentCreated)
            } catch (throwable: Throwable) {
                val message = when (throwable) {
                    is AppointmentCreationException.ServiceUnavailable -> throwable.message
                        ?: "One or more selected services are no longer available. Refresh services and choose again."
                    else -> throwable.message ?: "Unable to create appointment"
                }
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submitErrorMessage = message,
                        errorMessage = message
                    )
                }
                _events.emit(AddAppointmentEvent.ShowError(message))
                if (throwable is AppointmentCreationException.ServiceUnavailable) {
                    _events.emit(AddAppointmentEvent.ServiceSelectionRejected(message))
                }
            }
        }
    }

    fun createAppointment() {
        val current = _uiState.value
        val selectedLocationId = current.selectedLocationId
        val selectedBikeId = current.selectedBikeId
        if (selectedBikeId == null) {
            emitCreateError("Select a bike before creating the appointment.")
            return
        }
        if (selectedLocationId == null) {
            emitCreateError("Select a store location before creating the appointment.")
            return
        }
        if (current.requestedServiceCount == 0) {
            emitCreateError("Select at least one service before creating the appointment.")
            return
        }
        val scheduledAt = current.scheduledAt
        if (scheduledAt == null) {
            emitCreateError("Select an appointment date before creating the appointment.")
            return
        }
        val todayUtcStart = LocalDate.now(ZoneOffset.UTC).atStartOfDay(ZoneOffset.UTC).toInstant()
        val selectedInstant = runCatching { Instant.parse(scheduledAt) }.getOrNull()
        if (selectedInstant == null || selectedInstant.isBefore(todayUtcStart)) {
            emitCreateError("Select today or a future date for the appointment.")
            return
        }

        submitAppointment(
            request = CreateAppointmentRequest(
                bikeId = selectedBikeId,
                storeLocationId = selectedLocationId,
                scheduledAt = scheduledAt,
                notes = current.notes.takeIf(String::isNotBlank),
                requestedServices = current.requestedServiceItems.map { (serviceId, serviceType) ->
                    RequestedServiceItem(
                        serviceId = serviceId,
                        serviceType = serviceType
                    )
                }
            )
        )
    }

    private fun emitCreateError(message: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(submitErrorMessage = message, errorMessage = message) }
            _events.emit(AddAppointmentEvent.ShowError(message))
        }
    }

    private fun Set<String>.toggle(id: String): Set<String> {
        return if (id in this) this - id else this + id
    }
}

data class AddAppointmentUiState(
    val isLoadingBikes: Boolean = false,
    val isLoadingStores: Boolean = false,
    val isLoadingServices: Boolean = false,
    val isLoadingRemoteCatalog: Boolean = false,
    val isUsingCachedCatalog: Boolean = false,
    val catalogLastUpdated: Long? = null,
    val catalogFetchErrorMessage: String? = null,
    val isSubmitting: Boolean = false,
    val submitErrorMessage: String? = null,
    val bikes: List<Bike> = emptyList(),
    val stores: List<Store> = emptyList(),
    val serviceCatalog: ServiceCatalog = ServiceCatalog(),
    val selectedStoreId: String? = null,
    val selectedLocationId: String? = null,
    val selectedBikeId: String? = null,
    val selectedPackageIds: Set<String> = emptySet(),
    val selectedProductIds: Set<String> = emptySet(),
    val scheduledAt: String? = null,
    val notes: String = "",
    val errorMessage: String? = null
) {
    val selectedStore: Store?
        get() = stores.firstOrNull { it.id == selectedStoreId }

    val availableLocations: List<StoreLocation>
        get() = selectedStore?.locations.orEmpty()

    val selectedLocation: StoreLocation?
        get() = availableLocations.firstOrNull { it.id == selectedLocationId }

    val selectedBike: Bike?
        get() = bikes.firstOrNull { it.id == selectedBikeId }

    val selectedPackages: List<ServicePackage>
        get() = serviceCatalog.packages.filter { it.id in selectedPackageIds }

    val selectedProducts: List<ServiceProduct>
        get() = serviceCatalog.products.filter { it.id in selectedProductIds }

    val requestedServiceCount: Int
        get() = selectedPackageIds.size + selectedProductIds.size

    val requestedServiceItems: List<Pair<String, String>>
        get() = selectedPackageIds.map { it to RequestedServiceType.Package.apiValue } +
            selectedProductIds.map { it to RequestedServiceType.SingleService.apiValue }
}

sealed interface AddAppointmentEvent {
    data object AppointmentCreated : AddAppointmentEvent
    data class ServiceSelectionRejected(val message: String) : AddAppointmentEvent
    data class ShowError(val message: String) : AddAppointmentEvent
}
