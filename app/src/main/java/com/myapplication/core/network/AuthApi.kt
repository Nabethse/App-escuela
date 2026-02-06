package com.myapplication.core.network

import com.myapplication.features.auth.data.datasource.remote.model.LoginRequest
import com.myapplication.features.auth.data.datasource.remote.model.RegisterRequest
import com.myapplication.features.auth.data.datasource.remote.model.AuthResponse
import com.myapplication.features.auth.data.datasource.remote.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("auth/users")
    suspend fun getUsers(@Header("Authorization") token: String): List<UserDto>

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") token: String): UserDto
}