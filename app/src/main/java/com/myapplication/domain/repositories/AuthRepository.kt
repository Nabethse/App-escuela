package com.myapplication.domain.repositories

import com.myapplication.data.model.AuthResponse
import com.myapplication.data.model.LoginRequest
import com.myapplication.data.model.RegisterRequest

interface AuthRepository {
    suspend fun login(request: LoginRequest): AuthResponse
    suspend fun register(request: RegisterRequest): AuthResponse
}