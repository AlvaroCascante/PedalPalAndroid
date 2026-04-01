package com.quetoquenana.and.features.authentication.data.remote.di

import com.quetoquenana.and.features.authentication.data.remote.api.AuthApi
import com.quetoquenana.and.features.authentication.data.remote.dataSource.AuthRemoteDataSource
import com.quetoquenana.and.features.authentication.data.remote.dataSource.AuthRemoteDataSourceRetrofit
import dagger.Binds
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

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRemoteModule {
    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        impl: AuthRemoteDataSourceRetrofit
    ): AuthRemoteDataSource
}