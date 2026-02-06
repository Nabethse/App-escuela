package com.myapplication.features.alumn.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.myapplication.features.alumn.data.datasource.remote.model.AlumnDto

@Composable
fun AlumnCard(
    alumn: AlumnDto,
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
            Text(text = alumn.name, style = MaterialTheme.typography.titleMedium)
            Text(text = alumn.email, style = MaterialTheme.typography.bodyMedium)
            alumn.grade?.let {
                Text(text = "Grado: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}