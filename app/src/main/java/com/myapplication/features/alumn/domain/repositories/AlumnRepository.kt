package com.myapplication.features.alumn.domain.repositories

import com.myapplication.features.alumn.data.datasource.remote.model.AlumnDto
import kotlinx.coroutines.flow.Flow

interface AlumnRepository {
    val allAlumns: Flow<List<AlumnDto>>
    suspend fun getAlumns(token: String): List<AlumnDto>?
    suspend fun getAlumn(token: String, id: Int): AlumnDto?
    suspend fun createAlumn(token: String, alumn: AlumnDto): AlumnDto?
    suspend fun updateAlumn(token: String, id: Int, alumn: AlumnDto): AlumnDto?
    suspend fun deleteAlumn(token: String, id: Int)
}
