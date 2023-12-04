package com.example.todo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.todo.entities.TypicalTodo
import com.example.todo.entities.typicalTodos
import com.example.todo.viewmodels.TodosViewModel

@Composable
fun TypicalTodosScreen(viewModel: TodosViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTodo by remember { mutableStateOf<TypicalTodo?>(null) }


    Column {
        Text(
            "Typical Tasks",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(typicalTodos) { todo ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        selectedTodo = todo
                        showDialog = true
                    }) {
                    Text(todo.title)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        selectedTodo = todo
                        showDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        }
    }

    if (showDialog) {
        selectedTodo?.let {
            Dialog(onDismissRequest = { showDialog = false }) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    TodoForm(
                        viewModel = viewModel,
                        onClose = { showDialog = false },
                        existingTodo = null,
                        predefinedTitle = it.title,
                        predefinedDescription = it.description
                    )
                }
            }
        }
    }
}


