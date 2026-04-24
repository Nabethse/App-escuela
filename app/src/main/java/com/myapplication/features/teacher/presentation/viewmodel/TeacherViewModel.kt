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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _eventFlow = MutableSharedFlow<TeacherEvent>()
    val eventFlow: SharedFlow<TeacherEvent> = _eventFlow.asSharedFlow()

    sealed class TeacherEvent {
        data class ShowToast(val message: String) : TeacherEvent()
        object SuccessVibration : TeacherEvent()
    }

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
                // Feedback instantáneo
                _eventFlow.emit(TeacherEvent.ShowToast("Profesor agregado correctamente"))
                _eventFlow.emit(TeacherEvent.SuccessVibration)
                
                createTeacherUseCase(token, TeacherDto(name = name, asignature = asignature))
            } catch (e: Exception) {
                _eventFlow.emit(TeacherEvent.ShowToast("Guardado localmente"))
                _eventFlow.emit(TeacherEvent.SuccessVibration)
            }
        }
    }

    fun updateTeacher(token: String, id: Int, name: String, asignature: String) {
        viewModelScope.launch {
            try {
                _eventFlow.emit(TeacherEvent.ShowToast("Profesor actualizado correctamente"))
                
                updateTeacherUseCase(token, id, TeacherDto(name = name, asignature = asignature))
            } catch (e: Exception) {
                _eventFlow.emit(TeacherEvent.ShowToast("Actualizado localmente"))
            }
        }
    }

    fun deleteTeacher(token: String, id: Int) {
        viewModelScope.launch {
            try {
                deleteTeacherUseCase(token, id)
                _eventFlow.emit(TeacherEvent.ShowToast("Profesor eliminado correctamente"))
            } catch (e: Exception) {
                _eventFlow.emit(TeacherEvent.ShowToast("Error al eliminar profesor"))
            }
        }
    }
}
