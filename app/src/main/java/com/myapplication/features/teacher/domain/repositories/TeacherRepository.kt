package com.myapplication.features.teacher.domain.repositories

import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto

interface TeacherRepository {
    suspend fun getTeachers(token: String): List<TeacherDto>?
    suspend fun getTeacher(token: String, id: Int): TeacherDto?
    suspend fun createTeacher(token: String, teacher: TeacherDto): TeacherDto?
    suspend fun updateTeacher(token: String, id: Int, teacher: TeacherDto): TeacherDto?
    suspend fun deleteTeacher(token: String, id: Int)
}
