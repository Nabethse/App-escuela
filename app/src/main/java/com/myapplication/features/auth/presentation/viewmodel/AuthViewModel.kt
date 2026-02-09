package com.myapplication.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapplication.features.auth.data.datasource.remote.model.LoginRequest
import com.myapplication.features.auth.data.datasource.remote.model.RegisterRequest
import com.myapplication.features.auth.domain.usecases.LoginUseCase
import com.myapplication.features.auth.domain.usecases.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = loginUseCase(LoginRequest(email, password))
                if (response.token != null) {
                    _authState.value = AuthState.Success(response.token)
                } else {
                    _authState.value = AuthState.Error("Login fallido: No se recibió token")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = registerUseCase(RegisterRequest(name, email, password))
                // Al registrarse, el servidor devuelve un mensaje de éxito, pero quizás no el token inmediatamente.
                // Si el servidor devuelve el token en el registro, lo usamos.
                if (response.token != null) {
                    _authState.value = AuthState.Success(response.token)
                } else {
                    // Si el registro fue exitoso pero no hay token, podemos indicar éxito de otra forma o pedir login.
                    // Según tu prueba de Insomnia, el registro devuelve un mensaje, no un token.
                    // Sin embargo, para que la app navegue al Home, necesitamos un token.
                    // Si el registro NO devuelve token, podrías cambiar el estado a algo como AuthState.Registered
                    _authState.value = AuthState.Error(response.message ?: "Registro exitoso, por favor inicia sesión")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}