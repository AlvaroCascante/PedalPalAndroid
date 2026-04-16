package com.quetoquenana.and.features.bikes.ui

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.domain.model.StravaBike

data class BikesUiState(
    val bikes: List<Bike> = emptyList(),
    val selectedType: BikeType? = null,
    val isLoading: Boolean = false
) {
    val filteredBikes: List<Bike>
        get() = selectedType?.let { type ->
            bikes.filter { it.type.equals(type.name, ignoreCase = true) }
        } ?: bikes
}

data class AddBikeUiState(
    val name: String = "",
    val type: String = "",
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val serialNumber: String = "",
    val notes: String = "",
    val isPublic: Boolean = false,
    val isSaving: Boolean = false
)

data class StravaImportUiState(
    val isConnecting: Boolean = false,
    val isLoadingBikes: Boolean = false,
    val isWaitingForAuthorization: Boolean = false,
    val bikes: List<StravaBike> = emptyList()
)

data class BikeDetailUiState(
    val bike: Bike? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class BikeHistoryUiState(
    val history: List<BikeHistory> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
