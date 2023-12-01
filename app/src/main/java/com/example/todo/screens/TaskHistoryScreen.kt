package com.example.todo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import com.example.tailtasks.enums.MoodScore
import com.example.todo.TaskRow
import com.example.todo.entities.TodoEntity
import com.example.todo.viewmodels.TodosViewModel


@Composable
fun CompletedTasksHistoryScreen(todosViewModel: TodosViewModel) {
    val completedTasks by todosViewModel.completedTasks.collectAsState()
    var showMoodDialog by remember { mutableStateOf(false) }
    var showTodoFormDialog by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf(MoodScore.NEUTRAL) }
    var currentTask by remember { mutableStateOf<TodoEntity?>(null) }

    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        completedTasks.forEach { task ->
            TaskRow(
                task = task,
                onTaskCheckedChange = { isChecked ->
                    if (isChecked) {
                        currentTask = task
                        showMoodDialog = true
                    } else {
                        todosViewModel.updateTask(task.copy(isCompleted = false))
                    }
                },
                onTaskClicked = {
                    currentTask = task
                    showTodoFormDialog = true
                }
            )
        }
    }

    if (showMoodDialog) {
        MoodSelectorDialog(
            onMoodSelected = { mood ->
                selectedMood = mood
            },
            onSubmit = {
                currentTask?.let {
                    todosViewModel.updateTask(it.copy(isCompleted = true, moodScore = selectedMood.score))
                }
                showMoodDialog = false
            },
            onDismiss = {
                showMoodDialog = false
            }
        )
    }

    if (showTodoFormDialog) {
        Dialog(onDismissRequest = { showTodoFormDialog = false }) {
            currentTask?.let {
                TodoForm(
                    viewModel = todosViewModel,
                    onClose = { showTodoFormDialog = false },
                    existingTodo = it
                )
            }
        }
    }
}