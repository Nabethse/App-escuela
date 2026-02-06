package com.myapplication.features.auth.domain.repositories

import com.myapplication.features.auth.data.datasource.remote.model.AuthResponse
import com.myapplication.features.auth.data.datasource.remote.model.LoginRequest
import com.myapplication.features.auth.data.datasource.remote.model.RegisterRequest

interface AuthRepository {
    suspend fun login(request: LoginRequest): AuthResponse
    suspend fun register(request: RegisterRequest): AuthResponse
}