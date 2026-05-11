package com.quetoquenana.and.features.services.data.local.di

import com.quetoquenana.and.core.database.AppDatabase
import com.quetoquenana.and.features.services.data.local.dao.ServiceCatalogDao
import com.quetoquenana.and.features.services.data.local.datasource.ServiceCatalogLocalDataSource
import com.quetoquenana.and.features.services.data.local.datasource.ServiceCatalogLocalDataSourceRoom
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceCatalogLocalDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindServiceCatalogLocalDataSource(
        impl: ServiceCatalogLocalDataSourceRoom
    ): ServiceCatalogLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object ServiceCatalogLocalDaoModule {
    @Provides
    fun provideServiceCatalogDao(database: AppDatabase): ServiceCatalogDao {
        return database.serviceCatalogDao()
    }
}
