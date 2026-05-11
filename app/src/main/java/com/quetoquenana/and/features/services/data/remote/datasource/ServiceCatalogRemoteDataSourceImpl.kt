package com.quetoquenana.and.features.services.data.remote.datasource

import com.quetoquenana.and.features.services.data.remote.api.ServiceCatalogApi
import com.quetoquenana.and.features.services.data.remote.dto.toDomain
import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import javax.inject.Inject

class ServiceCatalogRemoteDataSourceImpl @Inject constructor(
    private val api: ServiceCatalogApi
) : ServiceCatalogRemoteDataSource {

    override suspend fun getCatalog(): ServiceCatalog {
        val packages = api.getActivePackages().data.map { it.toDomain() }
        val products = api.getActiveProducts().data.map { it.toDomain() }
        return ServiceCatalog(packages = packages, products = products)
    }
}
