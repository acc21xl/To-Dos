package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todo.entities.TodoEntity
import com.example.todo.ui.theme.TodoTheme
import com.example.todo.viewmodels.SettingsViewModel
import com.example.todo.viewmodels.SettingsViewModelFactory


class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory((application as TodoApplication).todoDAO)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoTheme {

                // Pass the viewModel instance to ScreenScaffold
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(settingsViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { navController.navigate("history") }) {
                    Icon(Icons.Filled.History, contentDescription = "Completed Tasks")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { settingsViewModel.addTask("") },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        },
//        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        NavHost(navController, startDestination = "tasks", modifier = Modifier.padding(paddingValues)) {
            composable("tasks") { ActiveTasksScreen(settingsViewModel) }
            composable("history") {
                // Force UI refresh when navigating to the "history" screen
                    LaunchedEffect(Unit) {
                        settingsViewModel.loadCompletedTasks()
                    }
                    CompletedTasksHistoryScreen(settingsViewModel)
            }
        }
    }
}

@Composable
fun ActiveTasksScreen(settingsViewModel: SettingsViewModel) {
    val activeTasks by settingsViewModel.activeTasks.collectAsState()
    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        // Display only active tasks
        activeTasks.forEach { task ->
            TaskRow(task, onTaskCheckedChange = { isChecked ->
                settingsViewModel.updateTask(task.copy(isCompleted = isChecked))
            }, onTaskTextChange = { updatedText ->
                // Update the text for an existing task when the user finishes editing the text field
                settingsViewModel.updateTask(task.copy(text = updatedText))
            })
        }
    }
}
// Composable for completed tasks history
@Composable
fun CompletedTasksHistoryScreen(settingsViewModel: SettingsViewModel) {
    val completedTasks by settingsViewModel.completedTasks.collectAsState()

    Column {
        // Display completed tasks
        completedTasks.forEach { task ->
            TaskRow(task, onTaskCheckedChange = { isChecked ->
                settingsViewModel.updateTask(task.copy(isCompleted = isChecked))
            }, onTaskTextChange = { updatedText ->
                // Update the text for an existing task when the user finishes editing the text field
                settingsViewModel.updateTask(task.copy(text = updatedText))
            })
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskRow(
    task: TodoEntity,
    onTaskCheckedChange: (Boolean) -> Unit,
    onTaskTextChange: (String) -> Unit,

) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onTaskCheckedChange
        )
        TextField(
            value = task.text,
            onValueChange = onTaskTextChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp), // Ensure padding inside the TextField
            singleLine = true,
            placeholder = { Text("Enter task here")} // Ensure placeholder is visible
        )

    }
}



