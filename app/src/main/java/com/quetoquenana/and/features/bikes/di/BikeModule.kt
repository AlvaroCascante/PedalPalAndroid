package com.quetoquenana.and.features.bikes.di

import com.quetoquenana.and.features.authentication.domain.repository.AuthRepository
import com.quetoquenana.and.features.bikes.data.repository.BikeRepositoryImpl
import com.quetoquenana.and.features.bikes.domain.repository.BikeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BikeModule {

    @Binds
    @Singleton
    abstract fun bindsBikeRepository(
        repository: BikeRepositoryImpl
    ): BikeRepository
}