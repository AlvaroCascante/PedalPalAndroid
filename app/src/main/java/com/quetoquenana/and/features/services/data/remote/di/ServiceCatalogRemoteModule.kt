package com.quetoquenana.and.features.services.data.remote.di

import com.quetoquenana.and.features.services.data.remote.datasource.ServiceCatalogRemoteDataSource
import com.quetoquenana.and.features.services.data.remote.datasource.ServiceCatalogRemoteDataSourceRetrofit
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceCatalogRemoteModule {

    @Binds
    @Singleton
    abstract fun bindServiceCatalogRemoteDataSource(
        impl: ServiceCatalogRemoteDataSourceRetrofit
    ): ServiceCatalogRemoteDataSource
}
