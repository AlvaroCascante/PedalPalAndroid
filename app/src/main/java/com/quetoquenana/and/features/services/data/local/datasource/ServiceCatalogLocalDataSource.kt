package com.quetoquenana.and.features.services.data.local.datasource

import com.quetoquenana.and.features.services.data.local.entity.ServicePackageEntity
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageProductEntity
import com.quetoquenana.and.features.services.data.local.entity.ServiceProductEntity

interface ServiceCatalogLocalDataSource {
    suspend fun getProducts(storeLocationId: String): List<ServiceProductEntity>
    suspend fun getPackages(storeLocationId: String): List<ServicePackageEntity>
    suspend fun getProductsForPackage(storeLocationId: String, packageId: String): List<ServiceProductEntity>
    suspend fun getLastUpdated(storeLocationId: String): Long?
    suspend fun saveCatalog(
        storeLocationId: String,
        packages: List<ServicePackageEntity>,
        products: List<ServiceProductEntity>,
        packageProducts: List<ServicePackageProductEntity>,
        lastUpdated: Long
    )
}
