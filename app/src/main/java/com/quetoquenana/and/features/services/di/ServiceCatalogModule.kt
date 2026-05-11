package com.quetoquenana.and.features.services.di

import com.quetoquenana.and.features.services.data.remote.datasource.ServiceCatalogRemoteDataSource
import com.quetoquenana.and.features.services.data.remote.datasource.ServiceCatalogRemoteDataSourceImpl
import com.quetoquenana.and.features.services.data.repository.ServiceCatalogRepositoryImpl
import com.quetoquenana.and.features.services.domain.repository.ServiceCatalogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceCatalogModule {

    @Binds
    @Singleton
    abstract fun bindServiceCatalogRemoteDataSource(
        impl: ServiceCatalogRemoteDataSourceImpl
    ): ServiceCatalogRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindServiceCatalogRepository(
        impl: ServiceCatalogRepositoryImpl
    ): ServiceCatalogRepository
}
