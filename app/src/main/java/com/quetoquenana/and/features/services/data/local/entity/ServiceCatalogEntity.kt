package com.quetoquenana.and.features.services.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.quetoquenana.and.features.services.domain.model.ServicePackage
import com.quetoquenana.and.features.services.domain.model.ServiceProduct

@Entity(tableName = "service_products")
data class ServiceProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val price: String?,
    val status: String?,
    val isStandalone: Boolean,
    val updatedAt: Long
)

@Entity(tableName = "service_packages")
data class ServicePackageEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val price: String?,
    val status: String?,
    val updatedAt: Long
)

@Entity(
    tableName = "service_package_products",
    primaryKeys = ["packageId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = ServicePackageEntity::class,
            parentColumns = ["id"],
            childColumns = ["packageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("packageId"), Index("productId")]
)
data class ServicePackageProductEntity(
    val packageId: String,
    val productId: String
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

fun ServiceProduct.toEntity(currentTimeMillis: Long, isStandalone: Boolean): ServiceProductEntity {
    return ServiceProductEntity(
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

fun ServicePackage.toEntity(currentTimeMillis: Long): ServicePackageEntity {
    return ServicePackageEntity(
        id = id,
        name = name,
        description = description,
        price = price,
        status = status,
        updatedAt = currentTimeMillis
    )
}
