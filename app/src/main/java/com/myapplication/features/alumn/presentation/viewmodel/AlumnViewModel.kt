package com.myapplication.features.alumn.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapplication.core.util.LocationHelper
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
                        email = dto.email
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

    fun checkInLocation() {
        viewModelScope.launch {
            val location = locationHelper.getCurrentLocation()
            if (location != null) {
                val isInSchool = locationHelper.isLocationInSchool(location)
                if (isInSchool) {
                    // Aquí podrías llamar a un caso de uso para registrar la asistencia
                }
            }
        }
    }
}
