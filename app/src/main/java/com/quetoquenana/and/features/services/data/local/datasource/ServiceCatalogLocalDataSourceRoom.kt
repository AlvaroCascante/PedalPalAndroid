package com.quetoquenana.and.features.services.data.local.datasource

import com.quetoquenana.and.features.services.data.local.dao.ServiceCatalogDao
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageEntity
import com.quetoquenana.and.features.services.data.local.entity.ServicePackageProductEntity
import com.quetoquenana.and.features.services.data.local.entity.ServiceProductEntity
import javax.inject.Inject

class ServiceCatalogLocalDataSourceRoom @Inject constructor(
    private val dao: ServiceCatalogDao
) : ServiceCatalogLocalDataSource {
    override suspend fun getProducts(): List<ServiceProductEntity> = dao.getProducts()
    override suspend fun getPackages(): List<ServicePackageEntity> = dao.getPackages()
    override suspend fun getProductsForPackage(packageId: String): List<ServiceProductEntity> {
        return dao.getProductsForPackage(packageId)
    }

    override suspend fun saveCatalog(
        packages: List<ServicePackageEntity>,
        products: List<ServiceProductEntity>,
        packageProducts: List<ServicePackageProductEntity>
    ) {
        dao.replaceAll(
            packages = packages,
            products = products,
            packageProducts = packageProducts
        )
    }
}
