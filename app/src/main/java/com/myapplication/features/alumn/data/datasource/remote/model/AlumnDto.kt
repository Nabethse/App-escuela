package com.myapplication.features.alumn.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class AlumnDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("grade") val grade: String? = null
)