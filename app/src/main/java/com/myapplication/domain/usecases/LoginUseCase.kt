package com.myapplication.domain.usecases

import com.myapplication.data.model.LoginRequest
import com.myapplication.domain.repositories.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: LoginRequest) = repository.login(request)
}