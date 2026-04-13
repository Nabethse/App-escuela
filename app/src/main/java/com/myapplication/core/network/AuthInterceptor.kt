package com.myapplication.core.network

import com.myapplication.core.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        
        // No añadir token si es login o registro, o si ya tiene el header Authorization
        val path = originalRequest.url.encodedPath
        val hasAuthHeader = originalRequest.header("Authorization") != null

        if (!hasAuthHeader && !path.contains("login") && !path.contains("register")) {
            val token = runBlocking {
                userPreferencesRepository.userToken.first()
            }
            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}
