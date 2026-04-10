package com.myapplication.features.attendance.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val alumnId: Int,
    val alumnName: String,
    val date: String,
    val latitude: Double,
    val longitude: Double,
    val isSynced: Boolean = false
)
