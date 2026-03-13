package com.myapplication.features.teacher.data.repositories

import com.myapplication.core.network.TeacherApi
import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto
import com.myapplication.features.teacher.domain.repositories.TeacherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepositoryImpl @Inject constructor(
    private val teacherApi: TeacherApi
) : TeacherRepository {
    override suspend fun getTeachers(token: String): List<TeacherDto>? {
        return teacherApi.getTeachers(token)
    }

    override suspend fun getTeacher(token: String, id: Int): TeacherDto? {
        return teacherApi.getTeacher(token, id)
    }

    override suspend fun createTeacher(token: String, teacher: TeacherDto): TeacherDto? {
        return teacherApi.createTeacher(token, teacher)
    }

    override suspend fun updateTeacher(token: String, id: Int, teacher: TeacherDto): TeacherDto? {
        return teacherApi.updateTeacher(token, id, teacher)
    }

    override suspend fun deleteTeacher(token: String, id: Int) {
        teacherApi.deleteTeacher(token, id)
    }
}
