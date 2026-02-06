package com.myapplication.features.teacher.presentation.screens

data class TeacherUiModel(
    val id: Int?,
    val name: String,
    val asignature: String
)

sealed interface TeacherUiState {
    object Loading : TeacherUiState
    data class Success(val teachers: List<TeacherUiModel>) : TeacherUiState
    data class Error(val message: String) : TeacherUiState
}