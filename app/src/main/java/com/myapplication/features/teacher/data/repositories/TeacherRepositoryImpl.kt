package com.myapplication.features.teacher.data.repositories

import com.myapplication.core.network.TeacherApi
import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto
import com.myapplication.features.teacher.domain.repositories.TeacherRepository

class TeacherRepositoryImpl(
    private val teacherApi: TeacherApi
) : TeacherRepository {
    override suspend fun getTeachers(token: String): List<TeacherDto> = 
        teacherApi.getTeachers("Bearer $token")

    override suspend fun getTeacher(token: String, id: Int): TeacherDto = 
        teacherApi.getTeacher("Bearer $token", id)

    override suspend fun createTeacher(token: String, teacher: TeacherDto): TeacherDto = 
        teacherApi.createTeacher("Bearer $token", teacher)

    override suspend fun updateTeacher(token: String, id: Int, teacher: TeacherDto): TeacherDto = 
        teacherApi.updateTeacher("Bearer $token", id, teacher)

    override suspend fun deleteTeacher(token: String, id: Int) = 
        teacherApi.deleteTeacher("Bearer $token", id)
}