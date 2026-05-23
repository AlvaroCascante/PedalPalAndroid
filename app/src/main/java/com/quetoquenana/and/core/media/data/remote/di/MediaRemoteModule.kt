package com.quetoquenana.and.core.media.data.remote.di

import com.quetoquenana.and.core.media.data.remote.api.MediaApi
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaRemoteDataSource
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaRemoteDataSourceRetrofit
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaUploadRemoteDataSource
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaUploadRemoteDataSourceOkHttp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaRemoteModule {

    @Binds
    @Singleton
    abstract fun bindMediaRemoteDataSource(
        impl: MediaRemoteDataSourceRetrofit,
    ): MediaRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindMediaUploadRemoteDataSource(
        impl: MediaUploadRemoteDataSourceOkHttp,
    ): MediaUploadRemoteDataSource

    companion object {
        @Provides
        @Singleton
        fun provideMediaApi(
            @Named("pedalPalServiceRetrofit") retrofit: Retrofit,
        ): MediaApi {
            return retrofit.create(MediaApi::class.java)
        }

        @Provides
        @Singleton
        @Named("mediaUploadClient")
        fun provideMediaUploadClient(
            loggingInterceptor: HttpLoggingInterceptor,
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }
    }
}