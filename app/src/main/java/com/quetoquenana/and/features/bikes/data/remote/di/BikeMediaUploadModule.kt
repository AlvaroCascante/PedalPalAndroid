package com.quetoquenana.and.features.bikes.data.remote.di

import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeMediaUploadRemoteDataSource
import com.quetoquenana.and.features.bikes.data.remote.dataSource.BikeMediaUploadRemoteDataSourceOkHttp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
abstract class BikeMediaUploadModule {

    @Binds
    @Singleton
    abstract fun bindBikeMediaUploadRemoteDataSource(
        impl: BikeMediaUploadRemoteDataSourceOkHttp
    ): BikeMediaUploadRemoteDataSource

    companion object {
        @Provides
        @Singleton
        @Named("bikeMediaUploadClient")
        fun provideBikeMediaUploadClient(
            loggingInterceptor: HttpLoggingInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }
    }
}

