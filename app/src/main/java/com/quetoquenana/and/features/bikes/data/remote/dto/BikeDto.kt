package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BikeDto(
    val id: String,
    val name: String,
    val type: String,
    val status: String,
    val isPublic: Boolean,
    val isExternalSync: Boolean,
    val brand: String?,
    val model: String?,
    val year: Int?,
    val serialNumber: String?,
    val notes: String?,
    val odometerKm: Double,
    val usageTimeMinutes: Int,
    val externalGearId: String?,
    val externalSyncProvider: String
)

fun BikeDto.toDomain(): Bike {
    return Bike(
        id = id,
        name = name,
        type = type,
        status = status,
        isPublic = isPublic,
        isExternalSync = isExternalSync,
        brand = brand,
        model = model,
        year = year,
        serialNumber = serialNumber,
        notes = notes,
        odometerKm = odometerKm,
        usageTimeMinutes = usageTimeMinutes,
        externalGearId = externalGearId,
        externalSyncProvider = externalSyncProvider
    )
}
