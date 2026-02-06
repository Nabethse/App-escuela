package com.myapplication.features.auth.data.repositories

import com.myapplication.core.network.AuthApi
import com.myapplication.features.auth.data.datasource.remote.model.AuthResponse
import com.myapplication.features.auth.data.datasource.remote.model.LoginRequest
import com.myapplication.features.auth.data.datasource.remote.model.RegisterRequest
import com.myapplication.features.auth.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {
    override suspend fun login(request: LoginRequest): AuthResponse {
        return authApi.login(request)
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        return authApi.register(request)
    }
}