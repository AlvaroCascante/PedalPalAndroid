package com.quetoquenana.and.features.bikes.data.remote.di

import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeRemoteDataSource
import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeRemoteDataSourceRetrofit
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BikeRemoteModule {
    @Binds
    @Singleton
    abstract fun bindBikeRemoteDataSource(
        impl: BikeRemoteDataSourceRetrofit
    ): BikeRemoteDataSource
}
