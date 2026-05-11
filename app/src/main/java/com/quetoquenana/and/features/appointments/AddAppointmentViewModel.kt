package com.quetoquenana.and.features.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.appointments.domain.model.RequestedServiceType
import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import com.quetoquenana.and.features.services.domain.model.ServicePackage
import com.quetoquenana.and.features.services.domain.model.ServiceProduct
import com.quetoquenana.and.features.services.domain.usecase.GetServiceCatalogUseCase
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import com.quetoquenana.and.features.stores.domain.usecase.GetStoresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddAppointmentViewModel @Inject constructor(
    private val getStoresUseCase: GetStoresUseCase,
    private val getServiceCatalogUseCase: GetServiceCatalogUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAppointmentUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStores()
        loadServiceCatalog()
    }

    fun loadStores(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStores = true, errorMessage = null) }
            try {
                val stores = getStoresUseCase(refresh = refresh)
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

                    current.copy(
                        stores = stores,
                        selectedStoreId = selectedStoreId,
                        selectedLocationId = selectedLocationId,
                        isLoadingStores = false
                    )
                }
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
            val selectedStore = current.stores.firstOrNull { it.id == storeId }
            current.copy(
                selectedStoreId = storeId,
                selectedLocationId = selectedStore?.locations.orEmpty().singleOrNull()?.id
            )
        }
    }

    fun onLocationSelected(locationId: String) {
        _uiState.update { it.copy(selectedLocationId = locationId) }
    }

    fun loadServiceCatalog(refresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingServices = true, errorMessage = null) }
            try {
                val catalog = getServiceCatalogUseCase(refresh = refresh)
                _uiState.update {
                    it.copy(
                        serviceCatalog = catalog,
                        selectedPackageIds = it.selectedPackageIds.filter(catalog.packages.map(ServicePackage::id)::contains).toSet(),
                        selectedProductIds = it.selectedProductIds.filter(catalog.products.map(ServiceProduct::id)::contains).toSet(),
                        isLoadingServices = false
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoadingServices = false,
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

    private fun Set<String>.toggle(id: String): Set<String> {
        return if (id in this) this - id else this + id
    }
}

data class AddAppointmentUiState(
    val isLoadingStores: Boolean = false,
    val isLoadingServices: Boolean = false,
    val stores: List<Store> = emptyList(),
    val serviceCatalog: ServiceCatalog = ServiceCatalog(),
    val selectedStoreId: String? = null,
    val selectedLocationId: String? = null,
    val selectedPackageIds: Set<String> = emptySet(),
    val selectedProductIds: Set<String> = emptySet(),
    val notes: String = "",
    val errorMessage: String? = null
) {
    val selectedStore: Store?
        get() = stores.firstOrNull { it.id == selectedStoreId }

    val availableLocations: List<StoreLocation>
        get() = selectedStore?.locations.orEmpty()

    val selectedLocation: StoreLocation?
        get() = availableLocations.firstOrNull { it.id == selectedLocationId }

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
