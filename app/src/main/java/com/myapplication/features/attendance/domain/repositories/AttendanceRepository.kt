package com.myapplication.features.attendance.domain.repositories

import com.myapplication.features.attendance.data.datasource.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    val allAttendance: Flow<List<AttendanceEntity>>
    suspend fun saveAttendance(attendance: AttendanceEntity)
    suspend fun syncPendingAttendance(token: String)
}
