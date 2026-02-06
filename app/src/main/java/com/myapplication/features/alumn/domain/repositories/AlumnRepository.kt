package com.myapplication.features.alumn.domain.repositories

import com.myapplication.features.alumn.data.datasource.remote.model.AlumnDto

interface AlumnRepository {
    suspend fun getAlumns(token: String): List<AlumnDto>?
    suspend fun getAlumn(token: String, id: Int): AlumnDto?
    suspend fun createAlumn(token: String, alumn: AlumnDto): AlumnDto?
    suspend fun updateAlumn(token: String, id: Int, alumn: AlumnDto): AlumnDto?
    suspend fun deleteAlumn(token: String, id: Int)
}
