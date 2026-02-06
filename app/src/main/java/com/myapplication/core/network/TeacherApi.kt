package com.myapplication.core.network

import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto
import retrofit2.http.*

interface TeacherApi {
    @POST("teachers")
    suspend fun createTeacher(@Header("Authorization") token: String, @Body teacher: TeacherDto): TeacherDto?

    @GET("teachers")
    suspend fun getTeachers(@Header("Authorization") token: String): List<TeacherDto>?

    @GET("teachers/{id}")
    suspend fun getTeacher(@Header("Authorization") token: String, @Path("id") id: Int): TeacherDto?

    @PUT("teachers/{id}")
    suspend fun updateTeacher(@Header("Authorization") token: String, @Path("id") id: Int, @Body teacher: TeacherDto): TeacherDto?

    @DELETE("teachers/{id}")
    suspend fun deleteTeacher(@Header("Authorization") token: String, @Path("id") id: Int)
}
