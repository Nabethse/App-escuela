package com.myapplication.features.teacher.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val asignature: String
)
