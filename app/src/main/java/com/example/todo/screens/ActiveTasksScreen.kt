package com.example.todo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.tailtasks.enums.MoodScore
import com.example.todo.viewmodels.TodosViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.tailtasks.enums.Priority
import com.example.todo.TaskRow
import com.example.todo.entities.TodoEntity

@Composable
fun ActiveTasksScreen(todosViewModel: TodosViewModel) {
    val activeTasks by todosViewModel.activeTasks.collectAsState()
    var showMoodDialog by remember { mutableStateOf(false) }
    var showTodoFormDialog by remember { mutableStateOf(false) }
    var showTodoDisplayDialog by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf(MoodScore.NEUTRAL) }
    var currentTask by remember { mutableStateOf<TodoEntity?>(null) }
    val tasksGroupedByPriority = activeTasks.groupBy { it.priority }.toSortedMap(reverseOrder())
    val highestPriority = tasksGroupedByPriority.keys.firstOrNull()


    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tasksGroupedByPriority.forEach { (priority, tasks) ->
            item {
                PriorityDrawer(
                    priority = priority,
                    tasks = tasks,
                    isInitiallyExpanded = priority == highestPriority,
                    onTaskCheckedChange = { task, isChecked ->
                        if (isChecked) {
                            currentTask = task
                            showMoodDialog = true
                        } else {
                            todosViewModel.updateTask(task.copy(isCompleted = false))
                        }
                    },
                    onTaskClicked = { task ->
                        currentTask = task
                        showTodoFormDialog = true
                    },
                    onVisibilityClicked = { task ->
                        currentTask = task
                        showTodoDisplayDialog = true
                    }
                )
            }
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
                Box(
                    modifier = Modifier
                        .background(Color.White)
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

    if (showTodoDisplayDialog) {
        Dialog(onDismissRequest = { showTodoDisplayDialog = false }) {
            currentTask?.let {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    TodoDisplay(
                        viewModel = todosViewModel,
                        todo = it
                    )
                }
            }
        }
    }
}

@Composable
fun PriorityDrawer(
    priority: Priority,
    tasks: List<TodoEntity>,
    isInitiallyExpanded: Boolean,
    onTaskCheckedChange: (TodoEntity, Boolean) -> Unit,
    onTaskClicked: (TodoEntity) -> Unit,
    onVisibilityClicked: (TodoEntity) -> Unit
) {
    var isExpanded by remember { mutableStateOf(isInitiallyExpanded) }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            AlertBadge(count = tasks.size)
            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }
            Text(text = "${priority.name} PRIORITY TASKS", style = MaterialTheme.typography.labelMedium)
        }

        if (isExpanded) {
            tasks.forEach { task ->
                TaskRow(
                    task = task,
                    onTaskCheckedChange = { isChecked -> onTaskCheckedChange(task, isChecked) },
                    onTaskClicked = { onTaskClicked(task) },
                    onVisibilityClicked = { onVisibilityClicked(task) }
                )
            }
        }
    }
}

@Composable
fun AlertBadge(count: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(24.dp)
            .background(Color.Red, shape = CircleShape)
    ) {
        Text(text = count.toString(), color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun MoodSelectorDialog(onMoodSelected: (MoodScore) -> Unit, onSubmit: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Please score your dog's mood during this task") },
            text = {
                MoodSelector(onMoodSelected)
            },
            confirmButton = {
                Button(onClick = onSubmit) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
    )
}


@Composable
fun MoodSelector(onMoodSelected: (MoodScore) -> Unit) {
    var sliderPosition by remember { mutableStateOf(3f) } // Default to NEUTRAL
    val moodScore = getMoodFromSliderPosition(sliderPosition.toInt())

    Column {
        Text("Select Mood: ${moodScore.name}")
        Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished = {
                    onMoodSelected(moodScore)
                },
                valueRange = 1f..5f,
                steps = 3
        )
    }
}

fun getMoodFromSliderPosition(position: Int): MoodScore {
    return when (position) {
        1 -> MoodScore.UPSET
        2 -> MoodScore.SAD
        3 -> MoodScore.NEUTRAL
        4 -> MoodScore.HAPPY
        5 -> MoodScore.EXCELLENT
        else -> MoodScore.NEUTRAL
    }
}