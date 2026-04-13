package com.myapplication.features.alumn.data.repositories

import com.myapplication.core.network.AlumnApi
import com.myapplication.features.alumn.data.datasource.local.dao.AlumnDao
import com.myapplication.features.alumn.data.datasource.local.entity.AlumnEntity
import com.myapplication.features.alumn.data.datasource.remote.model.AlumnDto
import com.myapplication.features.alumn.domain.repositories.AlumnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlumnRepositoryImpl @Inject constructor(
    private val alumnApi: AlumnApi,
    private val alumnDao: AlumnDao
) : AlumnRepository {

    // Offline-first: UI observes Room
    override val allAlumns: Flow<List<AlumnDto>> = alumnDao.getAllAlumns().map { entities ->
        entities.map { it.toDto() }
    }

    override suspend fun getAlumns(token: String): List<AlumnDto>? {
        return try {
            val remoteAlumns = alumnApi.getAlumns(token)
            if (remoteAlumns != null) {
                // Cache refresh
                alumnDao.deleteAllAlumns()
                alumnDao.insertAlumns(remoteAlumns.map { it.toEntity() })
            }
            remoteAlumns
        } catch (e: Exception) {
            // On error, the UI will still have the local data from 'allAlumns' flow
            null
        }
    }

    override suspend fun getAlumn(token: String, id: Int): AlumnDto? {
        return alumnApi.getAlumn(token, id)
    }

    override suspend fun createAlumn(token: String, alumn: AlumnDto): AlumnDto? {
        return alumnApi.createAlumn(token, alumn)
    }

    override suspend fun updateAlumn(token: String, id: Int, alumn: AlumnDto): AlumnDto? {
        val updated = alumnApi.updateAlumn(token, id, alumn)
        if (updated != null) {
            alumnDao.insertAlumn(updated.toEntity())
        }
        return updated
    }

    override suspend fun deleteAlumn(token: String, id: Int) {
        alumnApi.deleteAlumn(token, id)
        alumnDao.getAlumnById(id)?.let { alumnDao.deleteAlumn(it) }
    }

    // Mappers
    private fun AlumnDto.toEntity() = AlumnEntity(
        id = id ?: 0,
        name = name,
        matricula = matricula,
        email = email,
        photoPath = photoPath
    )

    private fun AlumnEntity.toDto() = AlumnDto(
        id = id,
        name = name,
        matricula = matricula,
        email = email,
        photoPath = photoPath
    )
}
