package com.quetoquenana.and.features.bikes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quetoquenana.and.features.bikes.domain.model.Bike

@Entity(tableName = "bikes")
data class BikeEntity(
    @PrimaryKey val id: String,
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
    val updatedAt: Long
)

fun BikeEntity.toDomain(): Bike {
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

fun Bike.toEntity(currentTimeMillis: Long): BikeEntity {
    return BikeEntity(
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
        updatedAt = currentTimeMillis
    )
}
