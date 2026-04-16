package com.quetoquenana.and.features.bikes.data.remote.dto

import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeComponent
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
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
    val externalSyncProvider: String,
    val components: Set<BikeComponentDto> = emptySet()
)

@JsonClass(generateAdapter = true)
data class BikeComponentDto(
    val id: String,
    val type: String,
    val name: String,
    val status: String,
    val brand: String?,
    val model: String?,
    val notes: String?,
    val odometerKm: Int,
    val usageTimeMinutes: Int
)

@JsonClass(generateAdapter = true)
data class BikeHistoryDto(
    val id: String,
    val bikeId: String,
    val occurredAt: String,
    val performedBy: String?,
    val type: String,
    val payload: String
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
        externalSyncProvider = externalSyncProvider,
        components = components.map { it.toDomain() }
    )
}

fun BikeComponentDto.toDomain(): BikeComponent {
    return BikeComponent(
        id = id,
        type = type,
        name = name,
        status = status,
        brand = brand,
        model = model,
        notes = notes,
        odometerKm = odometerKm,
        usageTimeMinutes = usageTimeMinutes
    )
}

fun BikeHistoryDto.toDomain(): BikeHistory {
    return BikeHistory(
        id = id,
        bikeId = bikeId,
        occurredAt = occurredAt,
        performedBy = performedBy,
        type = type,
        payload = payload
    )
}
