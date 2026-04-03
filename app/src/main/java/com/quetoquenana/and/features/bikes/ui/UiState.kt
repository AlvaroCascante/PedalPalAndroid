package com.quetoquenana.and.features.bikes.ui

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.StravaBike

data class BikesUiState(
    val bikes: List<Bike> = emptyList(),
    val isLoading: Boolean = false
)

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

