package com.myapplication.features.alumn.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myapplication.features.alumn.domain.usecases.GetAlumnsUseCase
import com.myapplication.features.alumn.presentation.screens.AlumnUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlumnViewModel(
    private val getAlumnsUseCase: GetAlumnsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlumnUiState>(AlumnUiState.Loading)
    val uiState: StateFlow<AlumnUiState> = _uiState

    fun getAlumns(token: String) {
        viewModelScope.launch {
            _uiState.value = AlumnUiState.Loading
            try {
                val alumns = getAlumnsUseCase(token)
                _uiState.value = AlumnUiState.Success(alumns)
            } catch (e: Exception) {
                _uiState.value = AlumnUiState.Error(e.message ?: "Error al cargar alumnos")
            }
        }
    }
}

class AlumnViewModelFactory(
    private val getAlumnsUseCase: GetAlumnsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlumnViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlumnViewModel(getAlumnsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
