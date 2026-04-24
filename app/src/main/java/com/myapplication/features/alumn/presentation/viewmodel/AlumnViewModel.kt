package com.myapplication.features.alumn.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapplication.core.util.LocationHelper
import com.myapplication.features.attendance.domain.repositories.AttendanceRepository
import com.myapplication.features.alumn.data.datasource.remote.model.AlumnDto
import com.myapplication.features.alumn.data.repositories.AlumnRepositoryImpl
import com.myapplication.features.alumn.domain.repositories.AlumnRepository
import com.myapplication.features.alumn.domain.usecases.*
import com.myapplication.features.alumn.presentation.screens.AlumnUiModel
import com.myapplication.features.alumn.presentation.screens.AlumnUiState
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
class AlumnViewModel @Inject constructor(
    private val getAlumnsUseCase: GetAlumnsUseCase,
    private val createAlumnUseCase: CreateAlumnUseCase,
    private val updateAlumnUseCase: UpdateAlumnUseCase,
    private val deleteAlumnUseCase: DeleteAlumnUseCase,
    private val alumnRepository: AlumnRepository,
    private val attendanceRepository: AttendanceRepository,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlumnUiState>(AlumnUiState.Loading)
    val uiState: StateFlow<AlumnUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<AlumnEvent>()
    val eventFlow: SharedFlow<AlumnEvent> = _eventFlow.asSharedFlow()

    sealed class AlumnEvent {
        data class ShowToast(val message: String) : AlumnEvent()
        object SuccessVibration : AlumnEvent()
    }

    init {
        observeAlumns()
    }

    private fun observeAlumns() {
        viewModelScope.launch {
            alumnRepository.allAlumns.collectLatest { alumns ->
                val uiAlumns = alumns.map { dto ->
                    AlumnUiModel(
                        id = dto.id,
                        name = dto.name,
                        matricula = dto.matricula,
                        email = dto.email,
                        photoPath = dto.photoPath
                    )
                }
                _uiState.value = AlumnUiState.Success(uiAlumns)
            }
        }
    }

    fun refreshAlumns(token: String) {
        viewModelScope.launch {
            try {
                val finalToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                getAlumnsUseCase(finalToken)
            } catch (e: Exception) {
            }
        }
    }

    fun createAlumn(token: String, name: String, matricula: String) {
        viewModelScope.launch {
            try {
                val finalToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                
                // 1. Actualización manual inmediata del UI State (Optimistic UI)
                val currentState = _uiState.value
                if (currentState is AlumnUiState.Success) {
                    val newList = currentState.alumns.toMutableList()
                    newList.add(0, AlumnUiModel(id = 0, name = name, matricula = matricula, email = ""))
                    _uiState.value = AlumnUiState.Success(newList)
                }

                _eventFlow.emit(AlumnEvent.ShowToast("Alumno agregado correctamente"))
                _eventFlow.emit(AlumnEvent.SuccessVibration)
                
                // 2. Ejecutar creación (Room notificará el flujo y reemplazará el optimista con el real)
                createAlumnUseCase(finalToken, AlumnDto(name = name, matricula = matricula))
            } catch (e: Exception) {
                _eventFlow.emit(AlumnEvent.ShowToast("Nota: Se guardó localmente"))
                _eventFlow.emit(AlumnEvent.SuccessVibration)
            }
        }
    }

    fun updateAlumn(token: String, id: Int, name: String, matricula: String) {
        viewModelScope.launch {
            try {
                val finalToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                
                // 1. Actualización optimista
                val currentState = _uiState.value
                if (currentState is AlumnUiState.Success) {
                    val newList = currentState.alumns.map {
                        if (it.id == id) it.copy(name = name, matricula = matricula) else it
                    }
                    _uiState.value = AlumnUiState.Success(newList)
                }

                _eventFlow.emit(AlumnEvent.ShowToast("Alumno actualizado correctamente"))
                
                updateAlumnUseCase(finalToken, id, AlumnDto(name = name, matricula = matricula))
            } catch (e: Exception) {
                _eventFlow.emit(AlumnEvent.ShowToast("Nota: Actualizado localmente"))
            }
        }
    }

    fun deleteAlumn(token: String, id: Int) {
        viewModelScope.launch {
            try {
                val finalToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                deleteAlumnUseCase(finalToken, id)
                _eventFlow.emit(AlumnEvent.ShowToast("Alumno eliminado correctamente"))
                refreshAlumns(finalToken)
            } catch (e: Exception) {
                _eventFlow.emit(AlumnEvent.ShowToast("Error al eliminar alumno"))
            }
        }
    }

    fun updateAlumnPhoto(token: String, id: Int, photoPath: String) {
        viewModelScope.launch {
            try {
                val finalToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val currentAlumn = (uiState.value as? AlumnUiState.Success)?.alumns?.find { it.id == id }
                if (currentAlumn != null) {
                    updateAlumnUseCase(finalToken, id, AlumnDto(
                        name = currentAlumn.name,
                        matricula = currentAlumn.matricula,
                        email = currentAlumn.email,
                        photoPath = photoPath
                    ))
                    refreshAlumns(finalToken)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun checkInLocation(alumnId: Int, alumnName: String) {
        viewModelScope.launch {
            val location = locationHelper.getCurrentLocation()
            if (location != null) {
                val isInSchool = locationHelper.isLocationInSchool(location)
                if (isInSchool) {
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                    val currentDate = sdf.format(java.util.Date())
                    
                    val attendance = com.myapplication.features.attendance.data.datasource.local.entity.AttendanceEntity(
                        alumnId = alumnId,
                        alumnName = alumnName,
                        date = currentDate,
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    attendanceRepository.saveAttendance(attendance)
                }
            }
        }
    }
}
