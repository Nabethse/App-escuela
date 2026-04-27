package com.myapplication.features.auth.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
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
import kotlinx.coroutines.tasks.await
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
                    userPreferencesRepository.saveToken(response.token) // <--- Guardamos el token para uso biométrico
                    flashManager.triggerFlash()
                    
                    // REGISTRO DE TOKEN FCM
                    try {
                        val fcmToken = FirebaseMessaging.getInstance().token.await()
                        Log.d("FCM_TOKEN", "Token obtenido: $fcmToken")
                        authRepository.updateFcmToken("Bearer ${response.token}", fcmToken)
                        Log.d("FCM_API", "✅ Token enviado al backend")
                        
                        // Suscripción al topic global por seguridad
                        FirebaseMessaging.getInstance().subscribeToTopic("global")
                            .addOnCompleteListener { Log.d("FCM_API", "Suscrito a topic global") }
                    } catch (e: Exception) {
                        Log.e("FCM_TOKEN", "❌ Error al obtener token: ${e.message}")
                    }

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
                    // Caso 1: El servidor registra e inicia sesión (devuelve token)
                    flashManager.triggerFlash()
                    updateFcmTokenOnServer(response.token)
                    _authState.value = AuthState.Success(response.token)
                } else {
                    // Caso 2: El servidor solo registra (devuelve mensaje pero no token)
                    val successMsg = response.message ?: "¡Registro exitoso! Por favor, inicia sesión."
                    _authState.value = AuthState.RegisterSuccess(successMsg)
                }
            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    400 -> "Datos de registro inválidos"
                    409 -> "Este correo ya está registrado"
                    else -> "Error del servidor (${e.code()})"
                }
                _authState.value = AuthState.Error(errorMsg)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de conexión: ${e.localizedMessage}")
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
                    updateFcmTokenOnServer(token)
                    _authState.value = AuthState.Success(token)
                } else {
                    _authState.value = AuthState.Error("No hay una sesión activa. Por favor, inicia sesión con tu correo.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error al recuperar la sesión: ${e.message}")
            }
        }
    }

    fun updateFcmTokenOnServer(authToken: String) {
        viewModelScope.launch {
            try {
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                authRepository.updateFcmToken("Bearer $authToken", fcmToken)
            } catch (e: Exception) {
                // Silently fail or log, FCM token update is not critical for login
                e.printStackTrace()
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

    suspend fun getSavedToken(): String? {
        return userPreferencesRepository.userToken.first()
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun testPushToUser(userId: Int, title: String, body: String) {
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.userToken.first()
                if (token != null) {
                    val result = authRepository.sendPushToUser(token, userId, title, body)
                    result.onSuccess {
                        _authState.value = AuthState.SuccessMessage("Push enviado correctamente")
                    }.onFailure {
                        _authState.value = AuthState.Error(it.message ?: "Error al enviar push")
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun testBroadcast(title: String, body: String) {
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.userToken.first()
                if (token != null) {
                    val result = authRepository.sendBroadcast(token, title, body)
                    result.onSuccess {
                        _authState.value = AuthState.SuccessMessage("Broadcast enviado correctamente")
                    }.onFailure {
                        _authState.value = AuthState.Error(it.message ?: "Error al enviar broadcast")
                    }
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
    data class SuccessMessage(val message: String) : AuthState()
    data class RegisterSuccess(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
