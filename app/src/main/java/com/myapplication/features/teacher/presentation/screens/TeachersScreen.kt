package com.myapplication.features.teacher.presentation.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.myapplication.features.teacher.presentation.components.TeacherCard
import com.myapplication.features.teacher.presentation.viewmodel.TeacherViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeachersScreen(
    viewModel: TeacherViewModel,
    token: String
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingTeacher by remember { mutableStateOf<TeacherUiModel?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getTeachers(token)
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is TeacherViewModel.TeacherEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is TeacherViewModel.TeacherEvent.SuccessVibration -> {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(200)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Profesores",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo Profesor") },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is TeacherUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TeacherUiState.Success -> {
                    if (state.teachers.isEmpty()) {
                        EmptyStateTeachers(
                            message = "No hay profesores registrados",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(state.teachers) { teacher ->
                                TeacherCard(
                                    teacher = teacher,
                                    onEdit = { editingTeacher = it },
                                    onDelete = { id -> viewModel.deleteTeacher(token, id) }
                                )
                            }
                        }
                    }
                }
                is TeacherUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
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

@Composable
fun EmptyStateTeachers(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.School, 
            contentDescription = null, 
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, color = MaterialTheme.colorScheme.outline)
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
