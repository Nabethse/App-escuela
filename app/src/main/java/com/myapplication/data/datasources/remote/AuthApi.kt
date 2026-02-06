package com.myapplication.data.datasources.remote

import com.myapplication.data.model.LoginRequest
import com.myapplication.data.model.RegisterRequest
import com.myapplication.data.model.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}