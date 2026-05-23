package com.quetoquenana.and.features.stores.data.remote.di

import com.quetoquenana.and.features.stores.data.remote.dataSource.StoreRemoteDataSource
import com.quetoquenana.and.features.stores.data.remote.dataSource.StoreRemoteDataSourceRetrofit
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreRemoteModule {

    @Binds
    @Singleton
    abstract fun bindStoreRemoteDataSource(
        impl: StoreRemoteDataSourceRetrofit
    ): StoreRemoteDataSource

}
