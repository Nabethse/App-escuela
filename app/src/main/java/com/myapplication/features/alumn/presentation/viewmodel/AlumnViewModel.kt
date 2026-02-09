package com.myapplication.features.alumn.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myapplication.features.alumn.data.datasource.remote.model.AlumnDto
import com.myapplication.features.alumn.domain.usecases.*
import com.myapplication.features.alumn.presentation.screens.AlumnUiModel
import com.myapplication.features.alumn.presentation.screens.AlumnUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlumnViewModel(
    private val getAlumnsUseCase: GetAlumnsUseCase,
    private val createAlumnUseCase: CreateAlumnUseCase,
    private val updateAlumnUseCase: UpdateAlumnUseCase,
    private val deleteAlumnUseCase: DeleteAlumnUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlumnUiState>(AlumnUiState.Loading)
    val uiState: StateFlow<AlumnUiState> = _uiState

    fun getAlumns(token: String) {
        viewModelScope.launch {
            _uiState.value = AlumnUiState.Loading
            try {
                val alumns = getAlumnsUseCase(token)
                val uiAlumns = alumns?.map { dto ->
                    AlumnUiModel(
                        id = dto.id,
                        name = dto.name,
                        // Si es un hash BCrypt, no se puede decifrar. 
                        // Aquí lo mostramos tal cual o podrías manejar un placeholder.
                        matricula = formatMatricula(dto.matricula), 
                        email = dto.email
                    )
                } ?: emptyList()
                _uiState.value = AlumnUiState.Success(uiAlumns)
            } catch (e: Exception) {
                _uiState.value = AlumnUiState.Error(e.message ?: "Error al cargar alumnos")
            }
        }
    }

    private fun formatMatricula(value: String): String {
        // Detectamos si es un hash de BCrypt (empiezan con $2a$)
        return if (value.startsWith("$2a$")) {
            "ID-" + value.takeLast(6) // Mostramos solo el final para que no sea tan largo
        } else {
            value // Si ya son números normales, los dejamos pasar
        }
    }

    fun createAlumn(token: String, name: String, matricula: String) {
        viewModelScope.launch {
            try {
                createAlumnUseCase(token, AlumnDto(name = name, matricula = matricula))
                getAlumns(token)
            } catch (e: Exception) {
            }
        }
    }

    fun updateAlumn(token: String, id: Int, name: String, matricula: String) {
        viewModelScope.launch {
            try {
                updateAlumnUseCase(token, id, AlumnDto(name = name, matricula = matricula))
                getAlumns(token)
            } catch (e: Exception) {
            }
        }
    }

    fun deleteAlumn(token: String, id: Int) {
        viewModelScope.launch {
            try {
                deleteAlumnUseCase(token, id)
                getAlumns(token)
            } catch (e: Exception) {
            }
        }
    }
}

class AlumnViewModelFactory(
    private val getAlumnsUseCase: GetAlumnsUseCase,
    private val createAlumnUseCase: CreateAlumnUseCase,
    private val updateAlumnUseCase: UpdateAlumnUseCase,
    private val deleteAlumnUseCase: DeleteAlumnUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlumnViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlumnViewModel(
                getAlumnsUseCase,
                createAlumnUseCase,
                updateAlumnUseCase,
                deleteAlumnUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
