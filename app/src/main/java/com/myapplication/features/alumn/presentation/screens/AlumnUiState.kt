package com.myapplication.features.alumn.presentation.screens

import com.myapplication.features.alumn.data.datasource.remote.model.AlumnDto

sealed interface AlumnUiState {
    object Loading : AlumnUiState
    data class Success(val alumns: List<AlumnDto>) : AlumnUiState
    data class Error(val message: String) : AlumnUiState
}