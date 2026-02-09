package com.myapplication.features.alumn.data.repositories

import com.myapplication.core.network.AlumnApi
import com.myapplication.features.alumn.data.datasource.remote.model.AlumnDto
import com.myapplication.features.alumn.domain.repositories.AlumnRepository

class AlumnRepositoryImpl(
    private val alumnApi: AlumnApi
) : AlumnRepository {
    override suspend fun getAlumns(token: String): List<AlumnDto>? = 
        alumnApi.getAlumns("Bearer $token")

    override suspend fun getAlumn(token: String, id: Int): AlumnDto? = 
        alumnApi.getAlumn("Bearer $token", id)

    override suspend fun createAlumn(token: String, alumn: AlumnDto): AlumnDto? = 
        alumnApi.createAlumn("Bearer $token", alumn)

    override suspend fun updateAlumn(token: String, id: Int, alumn: AlumnDto): AlumnDto? = 
        alumnApi.updateAlumn("Bearer $token", id, alumn)

    override suspend fun deleteAlumn(token: String, id: Int) = 
        alumnApi.deleteAlumn("Bearer $token", id)
}
