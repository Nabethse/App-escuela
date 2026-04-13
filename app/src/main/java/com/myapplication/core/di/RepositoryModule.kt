package com.myapplication.core.di

import com.myapplication.features.alumn.data.repositories.AlumnRepositoryImpl
import com.myapplication.features.alumn.domain.repositories.AlumnRepository
import com.myapplication.features.attendance.data.repositories.AttendanceRepositoryImpl
import com.myapplication.features.attendance.domain.repositories.AttendanceRepository
import com.myapplication.features.auth.data.repositories.AuthRepositoryImpl
import com.myapplication.features.auth.domain.repositories.AuthRepository
import com.myapplication.features.teacher.data.repositories.TeacherRepositoryImpl
import com.myapplication.features.teacher.domain.repositories.TeacherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAlumnRepository(
        alumnRepositoryImpl: AlumnRepositoryImpl
    ): AlumnRepository

    @Binds
    @Singleton
    abstract fun bindTeacherRepository(
        teacherRepositoryImpl: TeacherRepositoryImpl
    ): TeacherRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(
        attendanceRepositoryImpl: AttendanceRepositoryImpl
    ): AttendanceRepository
}
