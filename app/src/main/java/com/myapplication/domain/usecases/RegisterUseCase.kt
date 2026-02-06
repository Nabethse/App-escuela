package com.myapplication.domain.usecases

import com.myapplication.data.model.RegisterRequest
import com.myapplication.domain.repositories.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest) = repository.register(request)
}