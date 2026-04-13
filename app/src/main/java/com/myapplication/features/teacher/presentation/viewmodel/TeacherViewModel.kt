package com.myapplication.features.teacher.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto
import com.myapplication.features.teacher.domain.repositories.TeacherRepository
import com.myapplication.features.teacher.domain.usecases.*
import com.myapplication.features.teacher.presentation.screens.TeacherUiModel
import com.myapplication.features.teacher.presentation.screens.TeacherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherViewModel @Inject constructor(
    private val getTeachersUseCase: GetTeachersUseCase,
    private val createTeacherUseCase: CreateTeacherUseCase,
    private val updateTeacherUseCase: UpdateTeacherUseCase,
    private val deleteTeacherUseCase: DeleteTeacherUseCase,
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeacherUiState>(TeacherUiState.Loading)
    val uiState: StateFlow<TeacherUiState> = _uiState

    init {
        observeTeachers()
    }

    private fun observeTeachers() {
        viewModelScope.launch {
            teacherRepository.allTeachers.collectLatest { teachers ->
                val uiTeachers = teachers.map { dto ->
                    TeacherUiModel(
                        id = dto.id,
                        name = dto.name,
                        asignature = dto.asignature
                    )
                }
                _uiState.value = TeacherUiState.Success(uiTeachers)
            }
        }
    }

    fun getTeachers(token: String) {
        viewModelScope.launch {
            try {
                getTeachersUseCase(token)
            } catch (e: Exception) {
                if (_uiState.value !is TeacherUiState.Success) {
                    _uiState.value = TeacherUiState.Error(e.message ?: "Error al cargar profesores")
                }
            }
        }
    }

    fun createTeacher(token: String, name: String, asignature: String) {
        viewModelScope.launch {
            try {
                createTeacherUseCase(token, TeacherDto(name = name, asignature = asignature))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateTeacher(token: String, id: Int, name: String, asignature: String) {
        viewModelScope.launch {
            try {
                updateTeacherUseCase(token, id, TeacherDto(name = name, asignature = asignature))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteTeacher(token: String, id: Int) {
        viewModelScope.launch {
            try {
                deleteTeacherUseCase(token, id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
