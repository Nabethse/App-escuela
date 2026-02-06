package com.myapplication.features.teacher.presentation.screens

import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto

sealed interface TeacherUiState {
    object Loading : TeacherUiState
    data class Success(val teachers: List<TeacherDto>) : TeacherUiState
    data class Error(val message: String) : TeacherUiState
}