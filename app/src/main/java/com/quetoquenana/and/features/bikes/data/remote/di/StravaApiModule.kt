package com.quetoquenana.and.features.bikes.data.remote.di

import com.quetoquenana.and.features.bikes.data.remote.api.StravaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object StravaApiModule {

    @Provides
    @Singleton
    fun provideStravaApi(
        @Named("pedalPalServiceRetrofit") retrofit: Retrofit
    ): StravaApi {
        return retrofit.create(StravaApi::class.java)
    }
}
