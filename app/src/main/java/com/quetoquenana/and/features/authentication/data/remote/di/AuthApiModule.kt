package com.quetoquenana.and.features.authentication.data.remote.di

import com.quetoquenana.and.features.authentication.data.remote.api.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object AuthApiModule {
    @Provides
    @Singleton
    fun provideAuthApi(
        @Named("userServiceRetrofit")  retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}