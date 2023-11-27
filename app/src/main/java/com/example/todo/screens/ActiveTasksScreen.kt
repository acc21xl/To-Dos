package com.example.todo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.todo.viewmodels.TodosViewModel


@Composable
fun ActiveTasksScreen(todosViewModel: TodosViewModel) {
    val activeTasks by todosViewModel.activeTasks.collectAsState()
    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        activeTasks.forEach { task ->
            com.example.todo.TaskRow(task, onTaskCheckedChange = { isChecked ->
                todosViewModel.updateTask(task.copy(isCompleted = isChecked))
            }, onTaskTextChange = { updatedText ->
                todosViewModel.updateTask(task.copy(title = updatedText))
            })
        }
    }
}