package com.quetoquenana.and.features.services.data.remote.datasource

import com.quetoquenana.and.features.services.domain.model.ServiceCatalog

interface ServiceCatalogRemoteDataSource {
    suspend fun getCatalog(): ServiceCatalog
}
