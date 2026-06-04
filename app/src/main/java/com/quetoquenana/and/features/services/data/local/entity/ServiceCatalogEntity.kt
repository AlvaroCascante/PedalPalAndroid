package com.quetoquenana.and.features.services.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.quetoquenana.and.features.services.domain.model.ServicePackage
import com.quetoquenana.and.features.services.domain.model.ServiceProduct
import java.util.UUID

@Entity(
    tableName = "service_products",
    primaryKeys = ["storeLocationId", "id"],
    indices = [Index("storeLocationId")]
)
data class ServiceProductEntity(
    val storeLocationId: UUID,
    val id: UUID,
    val name: String,
    val description: String?,
    val price: String?,
    val status: String?,
    val isStandalone: Boolean,
    val updatedAt: Long
)

@Entity(
    tableName = "service_packages",
    primaryKeys = ["storeLocationId", "id"],
    indices = [Index("storeLocationId")]
)
data class ServicePackageEntity(
    val storeLocationId: UUID,
    val id: UUID,
    val name: String,
    val description: String?,
    val price: String?,
    val status: String?,
    val updatedAt: Long
)

@Entity(
    tableName = "service_package_products",
    primaryKeys = ["storeLocationId", "packageId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = ServicePackageEntity::class,
            parentColumns = ["storeLocationId", "id"],
            childColumns = ["storeLocationId", "packageId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceProductEntity::class,
            parentColumns = ["storeLocationId", "id"],
            childColumns = ["storeLocationId", "productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("storeLocationId", "packageId"), Index("storeLocationId", "productId")]
)
data class ServicePackageProductEntity(
    val storeLocationId: UUID,
    val packageId: UUID,
    val productId: UUID
)

fun ServiceProductEntity.toDomain(): ServiceProduct {
    return ServiceProduct(
        id = id,
        name = name,
        description = description,
        price = price,
        status = status
    )
}

fun ServiceProduct.toEntity(
    storeLocationId: UUID,
    currentTimeMillis: Long,
    isStandalone: Boolean
): ServiceProductEntity {
    return ServiceProductEntity(
        storeLocationId = storeLocationId,
        id = id,
        name = name,
        description = description,
        price = price,
        status = status,
        isStandalone = isStandalone,
        updatedAt = currentTimeMillis
    )
}

fun ServicePackageEntity.toDomain(products: List<ServiceProduct>): ServicePackage {
    return ServicePackage(
        id = id,
        name = name,
        description = description,
        price = price,
        status = status,
        products = products
    )
}

fun ServicePackage.toEntity(
    storeLocationId: UUID,
    currentTimeMillis: Long
): ServicePackageEntity {
    return ServicePackageEntity(
        storeLocationId = storeLocationId,
        id = id,
        name = name,
        description = description,
        price = price,
        status = status,
        updatedAt = currentTimeMillis
    )
}
