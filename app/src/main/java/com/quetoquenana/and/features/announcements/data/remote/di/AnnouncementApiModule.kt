package com.quetoquenana.and.features.announcements.data.remote.di

import com.quetoquenana.and.features.announcements.data.remote.api.AnnouncementApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AnnouncementApiModule {

    @Provides
    @Singleton
    fun provideAnnouncementApi(
        @Named("pedalPalServiceRetrofit") retrofit: Retrofit
    ): AnnouncementApi {
        return retrofit.create(AnnouncementApi::class.java)
    }
}
