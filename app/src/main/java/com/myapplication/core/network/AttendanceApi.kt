package com.myapplication.core.network

import com.myapplication.features.attendance.data.datasource.local.entity.AttendanceEntity
import retrofit2.http.*

interface AttendanceApi {
    @POST("attendance")
    suspend fun syncAttendance(@Header("Authorization") token: String, @Body attendance: AttendanceEntity): AttendanceEntity?
}
