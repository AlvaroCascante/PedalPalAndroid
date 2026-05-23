package com.quetoquenana.and.features.stores.di

import com.quetoquenana.and.features.stores.data.repository.StoreRepositoryImpl
import com.quetoquenana.and.features.stores.domain.repository.StoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreModule {

    @Binds
    @Singleton
    abstract fun bindStoreRepository(
        impl: StoreRepositoryImpl
    ): StoreRepository
}
