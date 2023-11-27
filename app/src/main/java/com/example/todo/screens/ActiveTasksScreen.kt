package com.example.todo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.todo.viewmodels.TodosViewModel


@Composable
fun ActiveTasksScreen(settingsViewModel: TodosViewModel) {
    val activeTasks by settingsViewModel.activeTasks.collectAsState()
    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        // Display only active tasks
        activeTasks.forEach { task ->
            com.example.todo.TaskRow(task, onTaskCheckedChange = { isChecked ->
                settingsViewModel.updateTask(task.copy(isCompleted = isChecked))
            }, onTaskTextChange = { updatedText ->
                // Update the text for an existing task when the user finishes editing the text field
                settingsViewModel.updateTask(task.copy(title = updatedText))
            })
        }
    }
}