package com.quetoquenana.and.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quetoquenana.and.features.authentication.data.local.dao.AuthSessionDao
import com.quetoquenana.and.features.authentication.data.local.dao.AuthUserDao
import com.quetoquenana.and.features.authentication.data.local.entity.AuthSessionEntity
import com.quetoquenana.and.features.authentication.data.local.entity.AuthUserEntity
import com.quetoquenana.and.features.bikes.data.local.dao.BikeDao
import com.quetoquenana.and.features.bikes.data.local.entity.BikeEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Database(
    entities = [
        AuthUserEntity::class,
        AuthSessionEntity::class,
        BikeEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authUserDao(): AuthUserDao
    abstract fun authSessionDao(): AuthSessionDao
    abstract fun bikeDao(): BikeDao
}

// DI Module to provide the AppDatabase instance
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pedalpal.db"
        ).fallbackToDestructiveMigration().build()
    }
}
