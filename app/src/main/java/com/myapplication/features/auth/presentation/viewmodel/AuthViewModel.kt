package com.myapplication.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapplication.core.data.UserPreferencesRepository
import com.myapplication.core.util.FlashManager
import com.myapplication.features.auth.data.datasource.remote.model.LoginRequest
import com.myapplication.features.auth.data.datasource.remote.model.RegisterRequest
import com.myapplication.features.auth.domain.repositories.AuthRepository
import com.myapplication.features.auth.domain.usecases.LoginUseCase
import com.myapplication.features.auth.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow as FlowStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val flashManager: FlashManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    val isBiometricEnabled: FlowStateFlow<Boolean> = userPreferencesRepository.isBiometricEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Por favor, completa todos los campos")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = loginUseCase(LoginRequest(email, password))
                if (!response.token.isNullOrBlank()) {
                    flashManager.triggerFlash()
                    _authState.value = AuthState.Success(response.token)
                } else {
                    _authState.value = AuthState.Error(response.message ?: "Error al iniciar sesión")
                }
            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    401 -> "Credenciales inválidas"
                    404 -> "Usuario no encontrado"
                    else -> "Error del servidor: ${e.code()}"
                }
                _authState.value = AuthState.Error(errorMsg)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de red: Verifica tu conexión")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Por favor, completa todos los campos")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = registerUseCase(RegisterRequest(name, email, password))
                if (!response.token.isNullOrBlank()) {
                    flashManager.triggerFlash()
                    _authState.value = AuthState.Success(response.token)
                } else if (response.message != null) {
                    _authState.value = AuthState.RegisterSuccess(response.message)
                } else {
                    _authState.value = AuthState.Error("Error en el registro")
                }
            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    400 -> "Datos de registro inválidos"
                    409 -> "el correo ya está registrado"
                    else -> "Error del servidor: ${e.code()}"
                }
                _authState.value = AuthState.Error(errorMsg)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun loginWithBiometrics() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val token = userPreferencesRepository.userToken.first()
                if (!token.isNullOrBlank()) {
                    flashManager.triggerFlash()
                    _authState.value = AuthState.Success(token)
                } else {
                    _authState.value = AuthState.Error("No hay una sesión activa. Por favor, inicia sesión con tu correo.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error al recuperar la sesión: ${e.message}")
            }
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setBiometricEnabled(enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class RegisterSuccess(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
