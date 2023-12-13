package com.example.todo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.todo.enums.MoodScore
import com.example.todo.TaskRow
import com.example.todo.entities.TodoEntity
import com.example.todo.viewmodels.TodosViewModel

@Composable
fun TodoHistory(todosViewModel: TodosViewModel) {
    // Displays a list of all completed tasks and provides options to view, edit,
    // and delete completed tasks
    // Also includes mood selection for completed tasks

    val completedTasks by todosViewModel.completedTasks.collectAsState()
    var showMoodDialog by remember { mutableStateOf(false) }
    var showTodoFormDialog by remember { mutableStateOf(false) }
    var showTodoDisplayDialog by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf(MoodScore.NEUTRAL) }
    var currentTask by remember { mutableStateOf<TodoEntity?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<TodoEntity?>(null) }

    Column {
        Text(
            "Completed Tasks",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(Modifier.height(4.dp))

        LazyColumn {
            items(completedTasks.size) { index ->
                val task = completedTasks[index]
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
                    },
                    onVisibilityClicked = {
                        currentTask = task
                        showTodoDisplayDialog = true
                    },
                    onDeleteClicked = {
                        taskToDelete = task
                        showDeleteConfirmDialog = true
                    }
                )
            }
        }
    }

    // This section shows a confirmation dialog before deleting a task
    if (showDeleteConfirmDialog && taskToDelete != null) {
        ConfirmDeleteDialog(task = taskToDelete!!, onConfirm = {
            todosViewModel.deleteTask(it.id.toLong())
            showDeleteConfirmDialog = false
            taskToDelete = null
        }, onDismiss = {
            showDeleteConfirmDialog = false
            taskToDelete = null
        })
    }

    // This section handles mood selection for a completed task
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

    // This section opens a dialog for editing a task's details
    if (showTodoFormDialog) {
        Dialog(onDismissRequest = { showTodoFormDialog = false }) {
            currentTask?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top)
                        .padding(vertical = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    TodoForm(
                        viewModel = todosViewModel,
                        onClose = { showTodoFormDialog = false },
                        existingTodo = it
                    )
                }
            }
        }
    }

    // This section opens a dialog for editing a task's details
    if (showTodoDisplayDialog) {
        Dialog(onDismissRequest = { showTodoDisplayDialog = false }) {
            currentTask?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top)
                        .padding(vertical = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    ViewTodo(
                        viewModel = todosViewModel,
                        todo = it
                    )
                }
            }
        }
    }
}