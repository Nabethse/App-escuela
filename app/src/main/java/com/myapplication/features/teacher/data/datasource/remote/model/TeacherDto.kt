package com.myapplication.features.teacher.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class TeacherDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("subject") val subject: String? = null
)