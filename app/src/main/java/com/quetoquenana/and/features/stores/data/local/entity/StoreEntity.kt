package com.quetoquenana.and.features.stores.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import java.util.UUID

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey val id: UUID,
    val name: String,
    val updatedAt: Long
)

@Entity(
    tableName = "store_locations",
    foreignKeys = [
        ForeignKey(
            entity = StoreEntity::class,
            parentColumns = ["id"],
            childColumns = ["storeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("storeId")]
)
data class StoreLocationEntity(
    @PrimaryKey val id: UUID,
    val storeId: UUID,
    val name: String,
    val currency: String,
    val storePrefix: String?,
    val website: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val phone: String?,
    val timezone: String?,
    val status: String?,
    val updatedAt: Long,
    val serviceCatalogLastUpdatedAt: Long? = null
)

fun StoreEntity.toDomain(locations: List<StoreLocationEntity>): Store {
    return Store(
        id = id,
        name = name,
        locations = locations.map { it.toDomain() }
    )
}

fun Store.toEntity(currentTimeMillis: Long): StoreEntity {
    return StoreEntity(
        id = id,
        name = name,
        updatedAt = currentTimeMillis
    )
}

fun StoreLocationEntity.toDomain(): StoreLocation {
    return StoreLocation(
        id = id,
        storeId = storeId,
        name = name,
        storePrefix = storePrefix,
        website = website,
        address = address,
        latitude = latitude,
        longitude = longitude,
        currency = currency,
        phone = phone,
        timezone = timezone,
        status = status,
        serviceCatalogLastUpdatedAt = serviceCatalogLastUpdatedAt
    )
}

fun StoreLocation.toEntity(
    currentTimeMillis: Long,
    serviceCatalogLastUpdatedAt: Long? = this.serviceCatalogLastUpdatedAt
): StoreLocationEntity {
    return StoreLocationEntity(
        id = id,
        storeId = storeId,
        name = name,
        storePrefix = storePrefix,
        website = website,
        address = address,
        latitude = latitude,
        longitude = longitude,
        phone = phone,
        timezone = timezone,
        currency = currency,
        status = status,
        updatedAt = currentTimeMillis,
        serviceCatalogLastUpdatedAt = serviceCatalogLastUpdatedAt
    )
}
