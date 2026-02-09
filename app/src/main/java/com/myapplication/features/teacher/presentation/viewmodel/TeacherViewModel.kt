package com.myapplication.features.teacher.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto
import com.myapplication.features.teacher.domain.usecases.*
import com.myapplication.features.teacher.presentation.screens.TeacherUiModel
import com.myapplication.features.teacher.presentation.screens.TeacherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherViewModel(
    private val getTeachersUseCase: GetTeachersUseCase,
    private val createTeacherUseCase: CreateTeacherUseCase,
    private val updateTeacherUseCase: UpdateTeacherUseCase,
    private val deleteTeacherUseCase: DeleteTeacherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeacherUiState>(TeacherUiState.Loading)
    val uiState: StateFlow<TeacherUiState> = _uiState

    fun getTeachers(token: String) {
        viewModelScope.launch {
            _uiState.value = TeacherUiState.Loading
            try {
                val teachers = getTeachersUseCase(token)
                val uiTeachers = teachers?.map { dto ->
                    TeacherUiModel(
                        id = dto.id,
                        name = dto.name,
                        asignature = dto.asignature
                    )
                } ?: emptyList()
                _uiState.value = TeacherUiState.Success(uiTeachers)
            } catch (e: Exception) {
                _uiState.value = TeacherUiState.Error(e.message ?: "Error al cargar profesores")
            }
        }
    }

    fun createTeacher(token: String, name: String, asignature: String) {
        viewModelScope.launch {
            try {
                createTeacherUseCase(token, TeacherDto(name = name, asignature = asignature))
                getTeachers(token) // Refresh list
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun updateTeacher(token: String, id: Int, name: String, asignature: String) {
        viewModelScope.launch {
            try {
                updateTeacherUseCase(token, id, TeacherDto(name = name, asignature = asignature))
                getTeachers(token)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteTeacher(token: String, id: Int) {
        viewModelScope.launch {
            try {
                deleteTeacherUseCase(token, id)
                getTeachers(token) // Refresh list
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
}

class TeacherViewModelFactory(
    private val getTeachersUseCase: GetTeachersUseCase,
    private val createTeacherUseCase: CreateTeacherUseCase,
    private val updateTeacherUseCase: UpdateTeacherUseCase,
    private val deleteTeacherUseCase: DeleteTeacherUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeacherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeacherViewModel(
                getTeachersUseCase,
                createTeacherUseCase,
                updateTeacherUseCase,
                deleteTeacherUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
