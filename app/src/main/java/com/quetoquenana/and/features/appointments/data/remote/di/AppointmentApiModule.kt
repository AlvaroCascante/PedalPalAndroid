package com.quetoquenana.and.features.appointments.data.remote.di

import com.quetoquenana.and.features.appointments.data.remote.api.AppointmentApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AppointmentApiModule {

    @Provides
    @Singleton
    fun provideAppointmentApi(
        @Named("pedalPalServiceRetrofit") retrofit: Retrofit
    ): AppointmentApi {
        return retrofit.create(AppointmentApi::class.java)
    }
}
