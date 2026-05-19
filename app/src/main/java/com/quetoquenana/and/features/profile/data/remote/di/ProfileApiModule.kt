package com.quetoquenana.and.features.profile.data.remote.di

import com.quetoquenana.and.features.profile.data.remote.api.ProfileApi
import com.quetoquenana.and.features.profile.data.remote.api.ProfileMediaApi
import com.quetoquenana.and.features.profile.data.remote.dataSource.ProfileRemoteDataSource
import com.quetoquenana.and.features.profile.data.remote.dataSource.ProfileRemoteDataSourceRetrofit
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
abstract class ProfileRemoteModule {

    @Binds
    @Singleton
    abstract fun bindProfileRemoteDataSource(
        impl: ProfileRemoteDataSourceRetrofit,
    ): ProfileRemoteDataSource
}

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

    @Provides
    @Singleton
    fun provideProfileMediaApi(
        @Named("pedalPalServiceRetrofit") retrofit: Retrofit,
    ): ProfileMediaApi {
        return retrofit.create(ProfileMediaApi::class.java)
    }
}