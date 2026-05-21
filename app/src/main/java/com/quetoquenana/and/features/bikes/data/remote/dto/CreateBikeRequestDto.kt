package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest

data class CreateBikeRequestDto(
    val name: String,
    val type: String,
    val brand: String?,
    val model: String?,
    val year: Int?,
    val serialNumber: String?,
    val notes: String?,
    val odometerKm: Int?,
    val usageTimeMinutes: Int?,
    val externalGearId: String?,
    val externalSyncProvider: String?,
    val isPublic: Boolean,
    val isExternalSync: Boolean
)

fun CreateBikeRequest.toDto(): CreateBikeRequestDto {
    return CreateBikeRequestDto(
        name = name,
        type = type,
        brand = brand,
        model = model,
        year = year,
        serialNumber = serialNumber,
        notes = notes,
        odometerKm = odometerKm,
        usageTimeMinutes = usageTimeMinutes,
        externalGearId = externalGearId,
        externalSyncProvider = externalSyncProvider,
        isPublic = isPublic,
        isExternalSync = isExternalSync
    )
}
