package com.quetoquenana.and.features.bikes.data.remote.di

import com.quetoquenana.and.features.bikes.data.remote.api.ComponentApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ComponentApiModule {
    @Provides
    @Singleton
    fun provideComponentTypeApi(
        @Named("pedalPalServiceRetrofit") retrofit: Retrofit
    ): ComponentApi {
        return retrofit.create(ComponentApi::class.java)
    }
}
