package com.example.todo.screens

import android.graphics.Bitmap
import androidx.compose.material3.Icon
import com.example.todo.viewmodels.TodosViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.todo.entities.TodoEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TodoDisplay(
    viewModel: TodosViewModel,
    todo: TodoEntity
) {
    val imageBitmap = todo.imageBytes?.let { byteArrayToBitmap(it) }
    val tags = viewModel.getTagsForTodo(todo.id.toLong())
    val tagsAsState by tags.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        BannerImageDisplay(imageBitmap)

        Text("${todo.title}", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))

        Text("Priority: ${todo.priority.name}", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))

        Text("${todo.description}", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))

        Text("To Complete By: ${todo.toCompleteByDate?.let { formatDate(it) }}", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))

        Text("Tags: ${tagsAsState.joinToString { it.title }}", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun BannerImageDisplay(imageBitmap: Bitmap?) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(if (imageBitmap != null) Color.Transparent else Color.LightGray)
    ) {
        imageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Todo Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "No Image",
            modifier = Modifier.size(36.dp)
        )
    }
}

fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(date)
}
