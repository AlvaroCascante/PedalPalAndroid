package com.quetoquenana.and.features.services.domain.repository

import com.quetoquenana.and.features.services.domain.model.ServiceCatalog

interface ServiceCatalogRepository {
    suspend fun getCatalog(refresh: Boolean = false): ServiceCatalog
}
