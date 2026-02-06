package com.myapplication.features.auth.data.datasource.remote.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)