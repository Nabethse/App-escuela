package com.myapplication.features.alumn.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alumns")
data class AlumnEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val matricula: String,
    val email: String?,
    val imageUrl: String? = null
)
