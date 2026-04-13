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

    init {
        observeAlumns()
        refreshAlumns()
    }

    private fun observeAlumns() {
        viewModelScope.launch {
            (alumnRepository as? AlumnRepositoryImpl)?.allAlumns?.collectLatest { alumns ->
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

    fun refreshAlumns() {
        viewModelScope.launch {
            try {
                getAlumnsUseCase("")
            } catch (e: Exception) {
            }
        }
    }

    fun createAlumn(name: String, matricula: String) {
        viewModelScope.launch {
            try {
                createAlumnUseCase("", AlumnDto(name = name, matricula = matricula))
                refreshAlumns()
            } catch (e: Exception) {
            }
        }
    }

    fun updateAlumn(id: Int, name: String, matricula: String) {
        viewModelScope.launch {
            try {
                updateAlumnUseCase("", id, AlumnDto(name = name, matricula = matricula))
                refreshAlumns()
            } catch (e: Exception) {
            }
        }
    }

    fun deleteAlumn(id: Int) {
        viewModelScope.launch {
            try {
                deleteAlumnUseCase("", id)
                refreshAlumns()
            } catch (e: Exception) {
            }
        }
    }

    fun updateAlumnPhoto(id: Int, photoPath: String) {
        viewModelScope.launch {
            try {
                val currentAlumn = (uiState.value as? AlumnUiState.Success)?.alumns?.find { it.id == id }
                if (currentAlumn != null) {
                    updateAlumnUseCase("", id, AlumnDto(
                        name = currentAlumn.name,
                        matricula = currentAlumn.matricula,
                        email = currentAlumn.email,
                        photoPath = photoPath
                    ))
                    refreshAlumns()
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
