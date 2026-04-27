package com.myapplication.features.auth.data.repositories

import com.myapplication.core.data.UserPreferencesRepository
import com.myapplication.core.network.AuthApi
import com.myapplication.core.network.FcmTokenRequest
import com.myapplication.features.auth.data.datasource.remote.model.AuthResponse
import com.myapplication.features.auth.data.datasource.remote.model.LoginRequest
import com.myapplication.features.auth.data.datasource.remote.model.RegisterRequest
import com.myapplication.features.auth.data.datasource.remote.model.SendPushRequest
import com.myapplication.features.auth.data.datasource.remote.model.SendBroadcastRequest
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

    override suspend fun updateFcmToken(token: String, fcmToken: String) {
        authApi.updateFcmToken(token, FcmTokenRequest(fcmToken))
    }

    override suspend fun sendPushToUser(
        jwt: String,
        userId: Int,
        title: String,
        body: String
    ): Result<Unit> {
        return try {
            val response = authApi.sendPushToUser(
                bearerToken = "Bearer $jwt",
                body = SendPushRequest(
                    user_id = userId,
                    title = title,
                    body = body,
                    data = mapOf(
                        "android_channel_id" to "canal_estudiantes",
                        "type" to "manual_test"
                    )
                )
            )
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception(response.errorBody()?.string() ?: "Error enviando push"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendBroadcast(
        jwt: String,
        title: String,
        body: String
    ): Result<Unit> {
        return try {
            val response = authApi.sendBroadcast(
                bearerToken = "Bearer $jwt",
                body = SendBroadcastRequest(
                    title = title,
                    body = body,
                    data = mapOf(
                        "android_channel_id" to "canal_estudiantes",
                        "type" to "broadcast_test"
                    )
                )
            )
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception(response.errorBody()?.string() ?: "Error enviando broadcast"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
