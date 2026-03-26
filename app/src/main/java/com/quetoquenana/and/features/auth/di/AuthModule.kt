package com.quetoquenana.and.features.auth.di

import com.quetoquenana.and.features.auth.data.repository.AuthRepositoryImpl
import com.quetoquenana.and.features.auth.data.repository.FirebaseRepositoryImpl
import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import com.quetoquenana.and.features.auth.domain.repository.FirebaseRepository
import com.quetoquenana.and.features.auth.session.TokenProvider
import com.quetoquenana.and.features.auth.session.TokenProviderImpl
import com.quetoquenana.and.features.auth.session.TokenStorage
import com.quetoquenana.and.features.auth.session.TokenStorageImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {


    @Binds
    @Singleton
    abstract fun bindsAuthRepository(
        authRepository: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenProvider(
        impl: TokenProviderImpl
    ): TokenProvider

    @Binds
    @Singleton
    abstract fun bindTokenStorage(
        impl: TokenStorageImpl
    ): TokenStorage


    @Binds
    @Singleton
    abstract fun bindAuthQueryService(
        impl: FirebaseRepositoryImpl
    ): FirebaseRepository
}