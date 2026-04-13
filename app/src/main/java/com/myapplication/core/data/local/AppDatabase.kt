package com.myapplication.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.myapplication.features.alumn.data.datasource.local.dao.AlumnDao
import com.myapplication.features.alumn.data.datasource.local.entity.AlumnEntity
import com.myapplication.features.attendance.data.datasource.local.dao.AttendanceDao
import com.myapplication.features.attendance.data.datasource.local.entity.AttendanceEntity
import com.myapplication.features.teacher.data.datasource.local.dao.TeacherDao
import com.myapplication.features.teacher.data.datasource.local.entity.TeacherEntity

@Database(entities = [AlumnEntity::class, TeacherEntity::class, AttendanceEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alumnDao(): AlumnDao
    abstract fun teacherDao(): TeacherDao
    abstract fun attendanceDao(): AttendanceDao
}
