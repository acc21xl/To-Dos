package com.example.todo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.example.todo.enums.MoodScore
import com.example.todo.viewmodels.TodosViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.todo.enums.Priority
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
    val scrollState = rememberScrollState()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<TodoEntity?>(null) }


    Column {
        Text(
            "Current Tasks",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
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
                    },
                    onDeleteTask = { task ->
                        taskToDelete = task
                        showDeleteConfirmDialog = true
                    }
                )
            }
        }
    }
    }

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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top)
                        .padding(vertical = 16.dp)
                        .verticalScroll(scrollState),
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
    onVisibilityClicked: (TodoEntity) -> Unit,
    onDeleteTask: (TodoEntity) -> Unit
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
                    onVisibilityClicked = { onVisibilityClicked(task) },
                    onDeleteClicked = { onDeleteTask(task) }
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
    var sliderPosition by remember { mutableStateOf(3f) }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Please score your dog's mood during this task") },
        text = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
            ) {
                MoodSelector(sliderPosition) { newPosition ->
                    sliderPosition = newPosition
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onMoodSelected(getMoodFromSliderPosition(sliderPosition.toInt()))
                onSubmit()
            }) {
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
fun MoodSelector(sliderPosition: Float, onSliderPositionChanged: (Float) -> Unit) {
    Column {
        Text("Select Mood: ${getMoodFromSliderPosition(sliderPosition.toInt()).name}")
        Slider(
            value = sliderPosition,
            onValueChange = onSliderPositionChanged,
            valueRange = 1f..5f,
            steps = 3
        )
    }
}

@Composable
fun ConfirmDeleteDialog(task: TodoEntity, onConfirm: (TodoEntity) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Delete") },
        text = { Text("Are you sure you want to delete the task '${task.title}'?") },
        confirmButton = {
            Button(onClick = { onConfirm(task) }) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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