package com.myapplication.features.auth.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

// Data Transfer Object for User
data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
)