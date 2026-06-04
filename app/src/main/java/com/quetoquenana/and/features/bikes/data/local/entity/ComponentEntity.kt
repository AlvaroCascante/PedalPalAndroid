package com.quetoquenana.and.features.bikes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.quetoquenana.and.features.bikes.domain.model.Component
import java.util.UUID

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
data class ComponentEntity(
    @PrimaryKey val id: UUID,
    val bikeId: UUID,
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

fun ComponentEntity.toDomain(): Component {
    return Component(
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

fun Component.toEntity(bikeId: UUID, currentTimeMillis: Long): ComponentEntity {
    return ComponentEntity(
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
