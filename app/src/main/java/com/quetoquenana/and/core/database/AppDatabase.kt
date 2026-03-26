package com.quetoquenana.and.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quetoquenana.and.features.auth.data.local.dao.AuthSessionDao
import com.quetoquenana.and.features.auth.data.local.dao.AuthUserDao
import com.quetoquenana.and.features.auth.data.local.entity.AuthSessionEntity
import com.quetoquenana.and.features.auth.data.local.entity.AuthUserEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Database(
    entities = [
        AuthUserEntity::class,
        AuthSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authUserDao(): AuthUserDao
    abstract fun authSessionDao(): AuthSessionDao
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
        ).build()
    }
}