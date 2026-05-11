package com.quetoquenana.and.features.services.data.local.datasource

import com.quetoquenana.and.features.services.data.local.entity.ServicePackageEntity
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageProductEntity
import com.quetoquenana.and.features.services.data.local.entity.ServiceProductEntity

interface ServiceCatalogLocalDataSource {
    suspend fun getProducts(): List<ServiceProductEntity>
    suspend fun getPackages(): List<ServicePackageEntity>
    suspend fun getProductsForPackage(packageId: String): List<ServiceProductEntity>
    suspend fun saveCatalog(
        packages: List<ServicePackageEntity>,
        products: List<ServiceProductEntity>,
        packageProducts: List<ServicePackageProductEntity>
    )
}
