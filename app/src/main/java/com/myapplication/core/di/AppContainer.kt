package com.myapplication.core.di

import com.myapplication.core.network.AlumnApi
import com.myapplication.core.network.AuthApi
import com.myapplication.core.network.TeacherApi
import com.myapplication.features.auth.data.repositories.AuthRepositoryImpl
import com.myapplication.features.auth.domain.repositories.AuthRepository
import com.myapplication.features.auth.domain.usecases.LoginUseCase
import com.myapplication.features.auth.domain.usecases.RegisterUseCase
import com.myapplication.features.auth.presentation.viewmodel.AuthViewModelFactory
import com.myapplication.features.teacher.data.repositories.TeacherRepositoryImpl
import com.myapplication.features.teacher.domain.repositories.TeacherRepository
import com.myapplication.features.teacher.domain.usecases.*
import com.myapplication.features.teacher.presentation.viewmodel.TeacherViewModelFactory
import com.myapplication.features.alumn.data.repositories.AlumnRepositoryImpl
import com.myapplication.features.alumn.domain.repositories.AlumnRepository
import com.myapplication.features.alumn.domain.usecases.*
import com.myapplication.features.alumn.presentation.viewmodel.AlumnViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val authViewModelFactory: AuthViewModelFactory
    val teacherViewModelFactory: TeacherViewModelFactory
    val alumnViewModelFactory: AlumnViewModelFactory
    
    val loginUseCase: LoginUseCase
    val registerUseCase: RegisterUseCase
}

class DefaultAppContainer : AppContainer {

    private val baseUrl = "https://antozac.store/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    private val teacherApi: TeacherApi by lazy {
        retrofit.create(TeacherApi::class.java)
    }

    private val alumnApi: AlumnApi by lazy {
        retrofit.create(AlumnApi::class.java)
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi)
    }

    private val teacherRepository: TeacherRepository by lazy {
        TeacherRepositoryImpl(teacherApi)
    }

    private val alumnRepository: AlumnRepository by lazy {
        AlumnRepositoryImpl(alumnApi)
    }

    override val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository)
    }

    override val registerUseCase: RegisterUseCase by lazy {
        RegisterUseCase(authRepository)
    }

    private val getTeachersUseCase: GetTeachersUseCase by lazy { GetTeachersUseCase(teacherRepository) }
    private val getAlumnsUseCase: GetAlumnsUseCase by lazy { GetAlumnsUseCase(alumnRepository) }

    // Factories
    override val authViewModelFactory: AuthViewModelFactory by lazy {
        AuthViewModelFactory(loginUseCase, registerUseCase)
    }

    override val teacherViewModelFactory: TeacherViewModelFactory by lazy {
        TeacherViewModelFactory(getTeachersUseCase)
    }

    override val alumnViewModelFactory: AlumnViewModelFactory by lazy {
        AlumnViewModelFactory(getAlumnsUseCase)
    }
}