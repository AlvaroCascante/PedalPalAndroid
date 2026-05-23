package com.quetoquenana.and.features.profile.data.local.di

import com.quetoquenana.and.core.database.AppDatabase
import com.quetoquenana.and.features.profile.data.local.dao.ProfileDao
import com.quetoquenana.and.features.profile.data.local.datasource.ProfileLocalDataSource
import com.quetoquenana.and.features.profile.data.local.datasource.ProfileLocalDataSourceRoom
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileLocalModule {

    @Binds
    @Singleton
    abstract fun bindProfileLocalDataSource(
        impl: ProfileLocalDataSourceRoom,
    ): ProfileLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object ProfileLocalDaoModule {

    @Provides
    fun provideProfileDao(database: AppDatabase): ProfileDao = database.profileDao()
}