package com.myapplication.core.di

import android.content.Context
import androidx.room.Room
import com.myapplication.core.data.local.AppDatabase
import com.myapplication.features.alumn.data.datasource.local.dao.AlumnDao
import com.myapplication.features.teacher.data.datasource.local.dao.TeacherDao
import com.myapplication.features.attendance.data.datasource.local.dao.AttendanceDao
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
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAlumnDao(database: AppDatabase): AlumnDao {
        return database.alumnDao()
    }

    @Provides
    fun provideTeacherDao(database: AppDatabase): TeacherDao {
        return database.teacherDao()
    }

    @Provides
    fun provideAttendanceDao(database: AppDatabase): AttendanceDao {
        return database.attendanceDao()
    }
}
