package com.myapplication.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myapplication.core.data.UserPreferencesRepository
import com.myapplication.core.util.FlashManager
import com.myapplication.features.auth.domain.repositories.AuthRepository
import com.myapplication.features.auth.domain.usecases.LoginUseCase
import com.myapplication.features.auth.domain.usecases.RegisterUseCase

class AuthViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val flashManager: FlashManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(
                loginUseCase,
                registerUseCase,
                authRepository,
                userPreferencesRepository,
                flashManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
