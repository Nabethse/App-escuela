package com.myapplication.features.alumn.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.myapplication.features.alumn.presentation.components.AlumnCard
import com.myapplication.features.alumn.presentation.viewmodel.AlumnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnsScreen(
    viewModel: AlumnViewModel,
    token: String
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAlumns(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Alumnos") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is AlumnUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AlumnUiState.Success -> {
                    LazyColumn {
                        items(state.alumns) { alumn ->
                            AlumnCard(alumn = alumn)
                        }
                    }
                }
                is AlumnUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}