package com.myapplication.features.auth.domain.usecases

import com.myapplication.features.auth.data.datasource.remote.model.LoginRequest
import com.myapplication.features.auth.domain.repositories.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: LoginRequest) = repository.login(request)
}