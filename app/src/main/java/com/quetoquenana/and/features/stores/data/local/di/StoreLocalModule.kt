package com.quetoquenana.and.features.stores.data.local.di

import com.quetoquenana.and.core.database.AppDatabase
import com.quetoquenana.and.features.stores.data.local.dao.StoreDao
import com.quetoquenana.and.features.stores.data.local.datasource.StoreLocalDataSource
import com.quetoquenana.and.features.stores.data.local.datasource.StoreLocalDataSourceRoom
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreLocalDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindStoreLocalDataSource(
        impl: StoreLocalDataSourceRoom
    ): StoreLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object StoreLocalDaoModule {

    @Provides
    fun provideStoreDao(database: AppDatabase): StoreDao = database.storeDao()
}
