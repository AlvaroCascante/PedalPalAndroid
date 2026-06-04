package com.quetoquenana.and.features.bikes.ui

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.model.BikeMedia
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import java.util.UUID

data class BikesUiState(
    val bikes: List<Bike> = emptyList(),
    val bikeProfileImageUrls: Map<UUID, String> = emptyMap(),
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
    val type: BikeType? = null,
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val serialNumber: String = "",
    val notes: String = "",
    val odometerKm: String = "",
    val importedStravaBikeId: String? = null,
    val importedStravaBikeName: String? = null,
    val stravaImport: StravaImportUiState = StravaImportUiState(),
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
    val isUploadingProfileImage: Boolean = false,
    val errorMessage: String? = null
)

data class AddBikeComponentUiState(
    val name: String = "",
    val type: String = "",
    val componentTypes: List<com.quetoquenana.and.features.bikes.domain.model.ComponentType> = emptyList(),
    val isLoadingComponentTypes: Boolean = false,
    val brand: String = "",
    val model: String = "",
    val notes: String = "",
    val odometerKm: String = "",
    val usageTimeMinutes: String = "",
    val isSaving: Boolean = false
)

data class BikeHistoryUiState(
    val history: List<BikeHistory> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class BikeMediaUiState(
    val media: List<BikeMedia> = emptyList(),
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val errorMessage: String? = null
)

