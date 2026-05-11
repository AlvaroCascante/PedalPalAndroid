package com.quetoquenana.and.features.stores.data.remote.di

import com.quetoquenana.and.features.stores.data.remote.api.StoreApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object StoreApiModule {

    @Provides
    @Singleton
    fun provideStoreApi(
        @Named("pedalPalServiceRetrofit") retrofit: Retrofit
    ): StoreApi {
        return retrofit.create(StoreApi::class.java)
    }
}
