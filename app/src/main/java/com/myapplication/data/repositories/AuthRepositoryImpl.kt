package com.myapplication.data.repositories

import com.myapplication.data.datasources.remote.AuthApi
import com.myapplication.data.model.AuthResponse
import com.myapplication.data.model.LoginRequest
import com.myapplication.data.model.RegisterRequest
import com.myapplication.domain.repositories.AuthRepository

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