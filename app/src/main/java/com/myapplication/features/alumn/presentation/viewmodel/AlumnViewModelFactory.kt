package com.myapplication.features.alumn.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myapplication.core.util.LocationHelper
import com.myapplication.features.alumn.domain.repositories.AlumnRepository
import com.myapplication.features.alumn.domain.usecases.CreateAlumnUseCase
import com.myapplication.features.alumn.domain.usecases.DeleteAlumnUseCase
import com.myapplication.features.alumn.domain.usecases.GetAlumnsUseCase
import com.myapplication.features.alumn.domain.usecases.UpdateAlumnUseCase

class AlumnViewModelFactory(
    private val getAlumnsUseCase: GetAlumnsUseCase,
    private val createAlumnUseCase: CreateAlumnUseCase,
    private val updateAlumnUseCase: UpdateAlumnUseCase,
    private val deleteAlumnUseCase: DeleteAlumnUseCase,
    private val alumnRepository: AlumnRepository,
    private val attendanceRepository: com.myapplication.features.attendance.domain.repositories.AttendanceRepository,
    private val locationHelper: LocationHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlumnViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlumnViewModel(
                getAlumnsUseCase,
                createAlumnUseCase,
                updateAlumnUseCase,
                deleteAlumnUseCase,
                alumnRepository,
                attendanceRepository,
                locationHelper
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
