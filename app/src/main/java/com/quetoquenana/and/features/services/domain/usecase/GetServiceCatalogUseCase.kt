package com.quetoquenana.and.features.services.domain.usecase

import com.quetoquenana.and.features.services.domain.model.ServiceCatalog
import com.quetoquenana.and.features.services.domain.repository.ServiceCatalogRepository
import javax.inject.Inject

class GetServiceCatalogUseCase @Inject constructor(
    private val repository: ServiceCatalogRepository
) {
    suspend operator fun invoke(refresh: Boolean = false): ServiceCatalog {
        return repository.getCatalog(refresh = refresh)
    }
}
