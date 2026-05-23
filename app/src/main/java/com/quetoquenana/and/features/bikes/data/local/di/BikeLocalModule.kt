package com.quetoquenana.and.features.bikes.data.local.di

import com.quetoquenana.and.core.database.AppDatabase
import com.quetoquenana.and.features.bikes.data.local.dao.ComponentDao
import com.quetoquenana.and.features.bikes.data.local.dao.BikeDao
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeComponentLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeComponentLocalDataSourceRoom
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSource
import com.quetoquenana.and.features.bikes.data.local.datasource.BikeLocalDataSourceRoom
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BikeLocalDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindBikeLocalDataSource(
        impl: BikeLocalDataSourceRoom
    ): BikeLocalDataSource

    @Binds
    @Singleton
    abstract fun bindBikeComponentLocalDataSource(
        impl: BikeComponentLocalDataSourceRoom
    ): BikeComponentLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object BikeLocalDaoModule {

    @Provides
    fun provideBikeDao(database: AppDatabase): BikeDao = database.bikeDao()

    @Provides
    fun provideBikeComponentDao(database: AppDatabase): ComponentDao = database.bikeComponentDao()
}
