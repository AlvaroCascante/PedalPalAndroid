package com.quetoquenana.and.features.authentication.data.local.di

import com.quetoquenana.and.core.database.AppDatabase
import com.quetoquenana.and.features.authentication.data.local.dao.AuthSessionDao
import com.quetoquenana.and.features.authentication.data.local.dao.AuthUserDao
import com.quetoquenana.and.features.authentication.data.local.datasource.AuthUserLocalDataSource
import com.quetoquenana.and.features.authentication.data.local.datasource.AuthUserLocalDataSourceRoom
import com.quetoquenana.and.features.authentication.data.local.datasource.SessionLocalDataSource
import com.quetoquenana.and.features.authentication.data.local.datasource.SessionLocalDataSourceRoom
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthLocalModule {
    @Binds
    @Singleton
    abstract fun bindSessionLocalDataSource(
        impl: SessionLocalDataSourceRoom
    ): SessionLocalDataSource

    @Binds
    @Singleton
    abstract fun bindAuthUserLocalDataSource(
        impl: AuthUserLocalDataSourceRoom
    ): AuthUserLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object AuthLocalDaoModule {
    @Provides
    fun provideAuthUserDao(
        database: AppDatabase
    ): AuthUserDao = database.authUserDao()

    @Provides
    fun provideAuthSessionDao(
        database: AppDatabase
    ): AuthSessionDao = database.authSessionDao()
}