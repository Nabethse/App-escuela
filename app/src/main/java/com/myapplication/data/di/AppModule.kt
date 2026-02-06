package com.myapplication.data.di

import com.myapplication.data.datasources.remote.AuthApi
import com.myapplication.data.repositories.AuthRepositoryImpl
import com.myapplication.domain.repositories.AuthRepository
import com.myapplication.domain.usecases.LoginUseCase
import com.myapplication.domain.usecases.RegisterUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val loginUseCase: LoginUseCase
    val registerUseCase: RegisterUseCase
}

class DefaultAppContainer : AppContainer {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://antozac.store/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi)
    }

    override val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository)
    }

    override val registerUseCase: RegisterUseCase by lazy {
        RegisterUseCase(authRepository)
    }
}