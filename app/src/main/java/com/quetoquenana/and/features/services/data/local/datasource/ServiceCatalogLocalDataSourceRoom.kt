package com.quetoquenana.and.features.services.data.local.datasource

import com.quetoquenana.and.features.services.data.local.dao.ServiceCatalogDao
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageEntity
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageProductEntity
import com.quetoquenana.and.features.services.data.local.entity.ServiceProductEntity
import java.util.UUID
import javax.inject.Inject

class ServiceCatalogLocalDataSourceRoom @Inject constructor(
    private val dao: ServiceCatalogDao
) : ServiceCatalogLocalDataSource {
    override suspend fun getProducts(storeLocationId: UUID): List<ServiceProductEntity> {
        return dao.getProducts(storeLocationId = storeLocationId)
    }

    override suspend fun getPackages(storeLocationId: UUID): List<ServicePackageEntity> {
        return dao.getPackages(storeLocationId = storeLocationId)
    }

    override suspend fun getProductsForPackage(
        storeLocationId: UUID,
        packageId: UUID
    ): List<ServiceProductEntity> {
        return dao.getProductsForPackage(storeLocationId = storeLocationId, packageId = packageId)
    }

    override suspend fun getLastUpdated(storeLocationId: UUID): Long? {
        return dao.getLastUpdated(storeLocationId = storeLocationId)
    }

    override suspend fun saveCatalog(
        storeLocationId: UUID,
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
