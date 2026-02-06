package com.myapplication.features.teacher.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myapplication.features.teacher.domain.usecases.GetTeachersUseCase
import com.myapplication.features.teacher.presentation.screens.TeacherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherViewModel(
    private val getTeachersUseCase: GetTeachersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeacherUiState>(TeacherUiState.Loading)
    val uiState: StateFlow<TeacherUiState> = _uiState

    fun getTeachers(token: String) {
        viewModelScope.launch {
            _uiState.value = TeacherUiState.Loading
            try {
                val teachers = getTeachersUseCase(token)
                _uiState.value = TeacherUiState.Success(teachers)
            } catch (e: Exception) {
                _uiState.value = TeacherUiState.Error(e.message ?: "Error al cargar profesores")
            }
        }
    }
}

class TeacherViewModelFactory(
    private val getTeachersUseCase: GetTeachersUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeacherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeacherViewModel(getTeachersUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
