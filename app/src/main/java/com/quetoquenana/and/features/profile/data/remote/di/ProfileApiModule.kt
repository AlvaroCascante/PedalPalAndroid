package com.quetoquenana.and.features.profile.data.remote.di

import com.quetoquenana.and.features.profile.data.remote.api.ProfileApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ProfileApiModule {

    @Provides
    @Singleton
    fun provideProfileApi(
        @Named("userServiceRetrofit") retrofit: Retrofit,
    ): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }
}