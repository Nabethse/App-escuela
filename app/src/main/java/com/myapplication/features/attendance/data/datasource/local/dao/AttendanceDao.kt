package com.myapplication.features.attendance.data.datasource.local.dao

import androidx.room.*
import com.myapplication.features.attendance.data.datasource.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance WHERE id = :id")
    suspend fun getAttendanceById(id: Int): AttendanceEntity?

    @Query("SELECT * FROM attendance")
    suspend fun getAllAttendanceList(): List<AttendanceEntity>

    @Delete
    suspend fun deleteAttendance(attendance: AttendanceEntity)
}
