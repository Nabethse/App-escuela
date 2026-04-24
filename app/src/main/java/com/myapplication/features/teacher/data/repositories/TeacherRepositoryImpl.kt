package com.myapplication.features.teacher.data.repositories

import com.myapplication.core.network.TeacherApi
import com.myapplication.features.teacher.data.datasource.local.dao.TeacherDao
import com.myapplication.features.teacher.data.datasource.local.entity.TeacherEntity
import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto
import com.myapplication.features.teacher.domain.repositories.TeacherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepositoryImpl @Inject constructor(
    private val teacherApi: TeacherApi,
    private val teacherDao: TeacherDao
) : TeacherRepository {

    override val allTeachers: Flow<List<TeacherDto>> = teacherDao.getAllTeachers().map { entities ->
        entities.map { it.toDto() }
    }

    override suspend fun getTeachers(token: String): List<TeacherDto>? {
        return try {
            val remoteTeachers = teacherApi.getTeachers(token)
            if (remoteTeachers != null) {
                teacherDao.deleteAllTeachers()
                teacherDao.insertTeachers(remoteTeachers.map { it.toEntity() })
            }
            remoteTeachers
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getTeacher(token: String, id: Int): TeacherDto? {
        return teacherApi.getTeacher(token, id)
    }

    override suspend fun createTeacher(token: String, teacher: TeacherDto): TeacherDto? {
        val created = try {
            teacherApi.createTeacher(token, teacher)
        } catch (e: Exception) {
            null
        }
        
        val entityToSave = if (created != null) {
            created.toEntity()
        } else {
            teacher.toEntity().copy(id = 0)
        }
        
        teacherDao.insertTeacher(entityToSave)
        return created ?: teacher
    }

    override suspend fun updateTeacher(token: String, id: Int, teacher: TeacherDto): TeacherDto? {
        val updated = try {
            teacherApi.updateTeacher(token, id, teacher)
        } catch (e: Exception) {
            null
        }

        val entityToSave = if (updated != null) {
            updated.toEntity()
        } else {
            teacher.toEntity().copy(id = id)
        }
        
        teacherDao.insertTeacher(entityToSave)
        return updated ?: teacher
    }

    override suspend fun deleteTeacher(token: String, id: Int) {
        teacherApi.deleteTeacher(token, id)
        teacherDao.getTeacherById(id)?.let { teacherDao.deleteTeacher(it) }
    }

    private fun TeacherDto.toEntity() = TeacherEntity(
        id = id ?: 0,
        name = name,
        asignature = asignature
    )

    private fun TeacherEntity.toDto() = TeacherDto(
        id = id,
        name = name,
        asignature = asignature
    )
}
