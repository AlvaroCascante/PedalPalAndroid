package com.quetoquenana.and.features.bikes.ui

data class AddBikePrefillArgs(
    val name: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val notes: String? = null,
    val odometerKm: String? = null,
    val externalGearId: String? = null
)

enum class AddBikeEntrySource {
    Manual,
    StravaImport
}

data class AddBikeRouteArgs(
    val prefill: AddBikePrefillArgs = AddBikePrefillArgs(),
    val entrySource: AddBikeEntrySource = AddBikeEntrySource.Manual
)
