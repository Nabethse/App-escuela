package com.myapplication.features.auth.data.repositories

import com.myapplication.core.data.UserPreferencesRepository
import com.myapplication.core.network.AuthApi
import com.myapplication.features.auth.data.datasource.remote.model.AuthResponse
import com.myapplication.features.auth.data.datasource.remote.model.LoginRequest
import com.myapplication.features.auth.data.datasource.remote.model.RegisterRequest
import com.myapplication.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userPreferencesRepository: UserPreferencesRepository
) : AuthRepository {
    override suspend fun login(request: LoginRequest): AuthResponse {
        val response = authApi.login(request)
        response.token?.let {
            userPreferencesRepository.saveToken(it)
        }
        return response
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        val response = authApi.register(request)
        response.token?.let {
            userPreferencesRepository.saveToken(it)
        }
        return response
    }

    override suspend fun logout() {
        userPreferencesRepository.clearToken()
    }
}
