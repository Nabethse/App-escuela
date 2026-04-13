package com.myapplication.features.attendance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapplication.core.util.LocationHelper
import com.myapplication.features.attendance.data.datasource.local.entity.AttendanceEntity
import com.myapplication.features.attendance.domain.repositories.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: AttendanceRepository,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _attendanceList = MutableStateFlow<List<AttendanceEntity>>(emptyList())
    val attendanceList: StateFlow<List<AttendanceEntity>> = _attendanceList

    init {
        viewModelScope.launch {
            repository.allAttendance.collectLatest {
                _attendanceList.value = it
            }
        }
    }

    fun registerAttendance(alumnId: Int, alumnName: String) {
        viewModelScope.launch {
            val location = locationHelper.getCurrentLocation()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val attendance = AttendanceEntity(
                alumnId = alumnId,
                alumnName = alumnName,
                date = currentDate,
                latitude = location?.latitude ?: 0.0,
                longitude = location?.longitude ?: 0.0
            )
            repository.saveAttendance(attendance)
        }
    }
}
