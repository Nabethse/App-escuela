package com.myapplication.core.di

import android.content.Context
import androidx.room.Room
import com.myapplication.core.data.local.AppDatabase
import com.myapplication.features.alumn.data.datasource.local.dao.AlumnDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "escuela_db"
        ).build()
    }

    @Provides
    fun provideAlumnDao(database: AppDatabase): AlumnDao {
        return database.alumnDao()
    }
}
