package com.myapplication.features.teacher.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myapplication.features.teacher.presentation.screens.TeacherUiModel

@Composable
fun TeacherCard(
    teacher: TeacherUiModel,
    onEdit: (TeacherUiModel) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = teacher.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Asignatura: ${teacher.asignature}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onEdit(teacher) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = { teacher.id?.let { onDelete(it) } }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
