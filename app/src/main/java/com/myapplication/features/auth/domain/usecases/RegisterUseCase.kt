package com.myapplication.features.auth.domain.usecases

import com.myapplication.features.auth.data.datasource.remote.model.RegisterRequest
import com.myapplication.features.auth.domain.repositories.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest) = repository.register(request)
}