package com.quetoquenana.and.pedalpal.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val BASE_URL: String = "https://example.com"

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BASE_URL
}