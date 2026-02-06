package com.myapplication.core.network

import com.myapplication.features.alumn.data.datasource.remote.model.AlumnDto
import retrofit2.http.*

interface AlumnApi {
    @POST("alumns")
    suspend fun createAlumn(@Header("Authorization") token: String, @Body alumn: AlumnDto): AlumnDto

    @GET("alumns")
    suspend fun getAlumns(@Header("Authorization") token: String): List<AlumnDto>

    @GET("alumns/{id}")
    suspend fun getAlumn(@Header("Authorization") token: String, @Path("id") id: Int): AlumnDto

    @PUT("alumns/{id}")
    suspend fun updateAlumn(@Header("Authorization") token: String, @Path("id") id: Int, @Body alumn: AlumnDto): AlumnDto

    @DELETE("alumns/{id}")
    suspend fun deleteAlumn(@Header("Authorization") token: String, @Path("id") id: Int)
}