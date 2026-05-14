package com.quetoquenana.and.features.bikes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.quetoquenana.and.features.bikes.domain.model.BikeComponent

@Entity(
    tableName = "bike_components",
    foreignKeys = [
        ForeignKey(
            entity = BikeEntity::class,
            parentColumns = ["id"],
            childColumns = ["bikeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["bikeId"])]
)
data class BikeComponentEntity(
    @PrimaryKey val id: String,
    val bikeId: String,
    val type: String,
    val name: String,
    val status: String,
    val brand: String?,
    val model: String?,
    val notes: String?,
    val odometerKm: Int,
    val usageTimeMinutes: Int,
    val updatedAt: Long
)

fun BikeComponentEntity.toDomain(): BikeComponent {
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

fun BikeComponent.toEntity(bikeId: String, currentTimeMillis: Long): BikeComponentEntity {
    return BikeComponentEntity(
        id = id,
        bikeId = bikeId,
        type = type,
        name = name,
        status = status,
        brand = brand,
        model = model,
        notes = notes,
        odometerKm = odometerKm,
        usageTimeMinutes = usageTimeMinutes,
        updatedAt = currentTimeMillis
    )
}
