package com.quetoquenana.and.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val USER_SERVICE_BASE_URL: String = "user-service.quetoquenana.com/userservice/api/"

    @Provides
    @Singleton
    fun provideBaseUrl(): String = USER_SERVICE_BASE_URL
}