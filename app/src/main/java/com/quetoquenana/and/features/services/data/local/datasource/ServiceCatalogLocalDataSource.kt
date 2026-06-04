package com.quetoquenana.and.features.services.data.local.datasource

import com.quetoquenana.and.features.services.data.local.entity.ServicePackageEntity
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageProductEntity
import com.quetoquenana.and.features.services.data.local.entity.ServiceProductEntity
import java.util.UUID

interface ServiceCatalogLocalDataSource {
    suspend fun getProducts(storeLocationId: UUID): List<ServiceProductEntity>
    suspend fun getPackages(storeLocationId: UUID): List<ServicePackageEntity>
    suspend fun getProductsForPackage(storeLocationId: UUID, packageId: UUID): List<ServiceProductEntity>
    suspend fun getLastUpdated(storeLocationId: UUID): Long?
    suspend fun saveCatalog(
        storeLocationId: UUID,
        packages: List<ServicePackageEntity>,
        products: List<ServiceProductEntity>,
        packageProducts: List<ServicePackageProductEntity>,
        lastUpdated: Long
    )
}
