package com.quetoquenana.and.features.stores.domain.usecase

import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.repository.StoreRepository
import javax.inject.Inject

class GetStoresUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(refresh: Boolean = false): List<Store> {
        return repository.getStores(refresh = refresh)
    }
}
