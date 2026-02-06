package com.myapplication.features.teacher.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myapplication.features.teacher.data.datasource.remote.model.TeacherDto

@Composable
fun TeacherCard(
    teacher: TeacherDto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = teacher.name, style = MaterialTheme.typography.titleMedium)
            Text(text = teacher.email, style = MaterialTheme.typography.bodyMedium)
            teacher.subject?.let {
                Text(text = "Materia: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}