package com.myapplication.features.alumn.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myapplication.features.alumn.presentation.screens.AlumnUiModel

@Composable
fun AlumnCard(
    alumn: AlumnUiModel,
    onEdit: (AlumnUiModel) -> Unit,
    onDelete: (Int) -> Unit,
    onCapturePhoto: (Int) -> Unit,
    onCheckIn: (Int) -> Unit,
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
                Text(text = alumn.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Matrícula: ${alumn.matricula}", style = MaterialTheme.typography.bodyMedium)
                alumn.email?.let {
                    Text(text = "Correo: $it", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = { alumn.id?.let { onCapturePhoto(it) } }) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Capturar Foto")
            }
            IconButton(onClick = { alumn.id?.let { onCheckIn(it) } }) {
                Icon(Icons.Default.LocationOn, contentDescription = "Check-in GPS")
            }
            IconButton(onClick = { onEdit(alumn) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = { alumn.id?.let { onDelete(it) } }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
