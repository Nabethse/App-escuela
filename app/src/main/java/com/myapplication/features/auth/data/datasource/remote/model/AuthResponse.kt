package com.myapplication.features.auth.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("user") val user: UserDto? = null
)