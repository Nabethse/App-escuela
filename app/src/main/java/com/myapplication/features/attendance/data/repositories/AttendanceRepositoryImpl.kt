package com.myapplication.features.attendance.data.repositories

import com.myapplication.core.network.AttendanceApi
import com.myapplication.features.attendance.data.datasource.local.dao.AttendanceDao
import com.myapplication.features.attendance.data.datasource.local.entity.AttendanceEntity
import com.myapplication.features.attendance.domain.repositories.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val attendanceApi: AttendanceApi
) : AttendanceRepository {

    override val allAttendance: Flow<List<AttendanceEntity>> = attendanceDao.getAllAttendance()

    override suspend fun saveAttendance(attendance: AttendanceEntity) {
        attendanceDao.insertAttendance(attendance)
    }

    override suspend fun syncPendingAttendance(token: String) {
        try {
            val localAttendance = attendanceDao.getAllAttendanceList()
            localAttendance.forEach { attendance ->
                val result = attendanceApi.syncAttendance(token, attendance)
                if (result != null) {
                    attendanceDao.deleteAttendance(attendance)
                }
            }
        } catch (e: Exception) {
            // Log or handle error
        }
    }
}
