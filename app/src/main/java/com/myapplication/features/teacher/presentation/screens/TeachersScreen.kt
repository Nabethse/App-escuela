package com.myapplication.features.teacher.presentation.screens

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
import com.myapplication.features.teacher.presentation.components.TeacherCard
import com.myapplication.features.teacher.presentation.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeachersScreen(
    viewModel: TeacherViewModel,
    token: String
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingTeacher by remember { mutableStateOf<TeacherUiModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getTeachers(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profesores") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Profesor")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is TeacherUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TeacherUiState.Success -> {
                    LazyColumn {
                        items(state.teachers) { teacher ->
                            TeacherCard(
                                teacher = teacher,
                                onEdit = { editingTeacher = it },
                                onDelete = { id -> viewModel.deleteTeacher(token, id) }
                            )
                        }
                    }
                }
                is TeacherUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        if (showDialog) {
            AddTeacherDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, asignature ->
                    viewModel.createTeacher(token, name, asignature)
                    showDialog = false
                }
            )
        }

        editingTeacher?.let { teacher ->
            EditTeacherDialog(
                teacher = teacher,
                onDismiss = { editingTeacher = null },
                onConfirm = { name, asignature ->
                    teacher.id?.let { id ->
                        viewModel.updateTeacher(token, id, name, asignature)
                    }
                    editingTeacher = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeacherDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var asignature by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Profesor") },
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
                    value = asignature,
                    onValueChange = { asignature = it },
                    label = { Text("Asignatura") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, asignature) },
                enabled = name.isNotBlank() && asignature.isNotBlank()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTeacherDialog(
    teacher: TeacherUiModel,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(teacher.name) }
    var asignature by remember { mutableStateOf(teacher.asignature) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Profesor") },
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
                    value = asignature,
                    onValueChange = { asignature = it },
                    label = { Text("Asignatura") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, asignature) },
                enabled = name.isNotBlank() && asignature.isNotBlank()
            ) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
