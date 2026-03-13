package com.myapplication.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.myapplication.features.alumn.data.datasource.local.dao.AlumnDao
import com.myapplication.features.alumn.data.datasource.local.entity.AlumnEntity

@Database(entities = [AlumnEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alumnDao(): AlumnDao
}
