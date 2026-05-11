package com.quetoquenana.and.features.services.data.remote.di

import com.quetoquenana.and.features.services.data.remote.api.ServiceCatalogApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ServiceCatalogApiModule {

    @Provides
    @Singleton
    fun provideServiceCatalogApi(
        @Named("pedalPalServiceRetrofit") retrofit: Retrofit
    ): ServiceCatalogApi = retrofit.create(ServiceCatalogApi::class.java)
}
