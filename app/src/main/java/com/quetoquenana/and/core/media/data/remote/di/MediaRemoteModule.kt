package com.quetoquenana.and.core.media.data.remote.di

import com.quetoquenana.and.core.media.data.remote.api.MediaApi
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaRemoteDataSource
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaRemoteDataSourceRetrofit
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaUploadDataSource
import com.quetoquenana.and.core.media.data.remote.dataSource.MediaUploadDataSourceOkHttp
import com.quetoquenana.and.core.utils.MODULE_MEDIA_UPLOAD_CLIENT
import com.quetoquenana.and.core.utils.MODULE_PEDALPAL_SERVICE_RETROFIT
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
        impl: MediaUploadDataSourceOkHttp,
    ): MediaUploadDataSource

    companion object {
        @Provides
        @Singleton
        fun provideMediaApi(
            @Named(value = MODULE_PEDALPAL_SERVICE_RETROFIT) retrofit: Retrofit,
        ): MediaApi {
            return retrofit.create(MediaApi::class.java)
        }

        @Provides
        @Singleton
        @Named(value = MODULE_MEDIA_UPLOAD_CLIENT)
        fun provideMediaUploadClient(
            loggingInterceptor: HttpLoggingInterceptor,
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }
    }
}