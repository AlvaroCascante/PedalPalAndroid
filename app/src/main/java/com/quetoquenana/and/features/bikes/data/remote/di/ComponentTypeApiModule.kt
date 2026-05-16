package com.quetoquenana.and.features.bikes.data.remote.di

import com.quetoquenana.and.features.bikes.data.remote.api.ComponentTypeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ComponentTypeApiModule {
    @Provides
    @Singleton
    fun provideComponentTypeApi(
        @Named("pedalPalServiceRetrofit") retrofit: Retrofit
    ): ComponentTypeApi {
        return retrofit.create(ComponentTypeApi::class.java)
    }
}
