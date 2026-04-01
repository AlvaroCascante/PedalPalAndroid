package com.quetoquenana.and.features.bikes.data.local.di

import com.quetoquenana.and.core.database.AppDatabase
import com.quetoquenana.and.features.bikes.data.local.dao.BikeDao
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
}

@Module
@InstallIn(SingletonComponent::class)
object BikeLocalDaoModule {

    @Provides
    fun provideBikeDao(database: AppDatabase): BikeDao = database.bikeDao()
}
