package com.myapplication.features.alumn.presentation.screens

data class AlumnUiModel(
    val id: Int?,
    val name: String,
    val matricula: String,
    val email: String?
)

sealed interface AlumnUiState {
    object Loading : AlumnUiState
    data class Success(val alumns: List<AlumnUiModel>) : AlumnUiState
    data class Error(val message: String) : AlumnUiState
}