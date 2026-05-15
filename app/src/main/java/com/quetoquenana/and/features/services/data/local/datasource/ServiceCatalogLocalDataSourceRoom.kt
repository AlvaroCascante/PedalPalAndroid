package com.quetoquenana.and.features.services.data.local.datasource

import com.quetoquenana.and.features.services.data.local.dao.ServiceCatalogDao
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageEntity
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageProductEntity
import com.quetoquenana.and.features.services.data.local.entity.ServiceProductEntity
import javax.inject.Inject

class ServiceCatalogLocalDataSourceRoom @Inject constructor(
    private val dao: ServiceCatalogDao
) : ServiceCatalogLocalDataSource {
    override suspend fun getProducts(storeLocationId: String): List<ServiceProductEntity> {
        return dao.getProducts(storeLocationId = storeLocationId)
    }

    override suspend fun getPackages(storeLocationId: String): List<ServicePackageEntity> {
        return dao.getPackages(storeLocationId = storeLocationId)
    }

    override suspend fun getProductsForPackage(
        storeLocationId: String,
        packageId: String
    ): List<ServiceProductEntity> {
        return dao.getProductsForPackage(storeLocationId = storeLocationId, packageId = packageId)
    }

    override suspend fun getLastUpdated(storeLocationId: String): Long? {
        return dao.getLastUpdated(storeLocationId = storeLocationId)
    }

    override suspend fun saveCatalog(
        storeLocationId: String,
        packages: List<ServicePackageEntity>,
        products: List<ServiceProductEntity>,
        packageProducts: List<ServicePackageProductEntity>,
        lastUpdated: Long
    ) {
        dao.replaceAll(
            storeLocationId = storeLocationId,
            packages = packages,
            products = products,
            packageProducts = packageProducts,
            lastUpdated = lastUpdated
        )
    }
}
