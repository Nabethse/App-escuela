package com.myapplication.features.alumn.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myapplication.features.alumn.presentation.components.AlumnCard
import com.myapplication.features.alumn.presentation.viewmodel.AlumnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnsScreen(
    viewModel: AlumnViewModel,
    token: String
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAlumn by remember { mutableStateOf<AlumnUiModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAlumns(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Alumnos") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Alumno")
            }
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
                            AlumnCard(
                                alumn = alumn,
                                onEdit = { editingAlumn = it },
                                onDelete = { id -> viewModel.deleteAlumn(token, id) }
                            )
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

        if (showAddDialog) {
            AlumnDialog(
                title = "Agregar Alumno",
                onDismiss = { showAddDialog = false },
                onConfirm = { name, matricula ->
                    viewModel.createAlumn(token, name, matricula)
                    showAddDialog = false
                }
            )
        }

        editingAlumn?.let { alumn ->
            AlumnDialog(
                title = "Editar Alumno",
                initialName = alumn.name,
                initialMatricula = alumn.matricula,
                onDismiss = { editingAlumn = null },
                onConfirm = { name, matricula ->
                    alumn.id?.let { viewModel.updateAlumn(token, it, name, matricula) }
                    editingAlumn = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnDialog(
    title: String,
    initialName: String = "",
    initialMatricula: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var matricula by remember { mutableStateOf(initialMatricula) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = matricula,
                    onValueChange = { matricula = it },
                    label = { Text("Matrícula") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, matricula) },
                enabled = name.isNotBlank() && matricula.isNotBlank()
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
