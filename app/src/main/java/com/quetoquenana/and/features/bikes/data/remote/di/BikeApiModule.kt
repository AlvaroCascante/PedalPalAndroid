package com.quetoquenana.and.features.bikes.data.remote.di

import com.quetoquenana.and.features.bikes.data.remote.api.BikeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object BikeApiModule {

    @Provides
    @Singleton
    fun provideBikeApi(
        @Named("pedalPalServiceRetrofit") retrofit: Retrofit
    ): BikeApi {
        return retrofit.create(BikeApi::class.java)
    }
}
