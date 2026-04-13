package com.myapplication.features.alumn.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alumns")
data class AlumnEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val matricula: String,
    val email: String? = null,
    val imageUrl: String? = null,
    val photoPath: String? = null
)
