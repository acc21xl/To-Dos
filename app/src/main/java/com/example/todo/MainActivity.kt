package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todo.entities.TodoEntity
import com.example.todo.screens.ActiveTasksScreen
import com.example.todo.screens.CompletedTasksHistoryScreen
import com.example.todo.screens.TodoForm
import com.example.todo.ui.theme.TodoTheme
import com.example.todo.viewmodels.TodosViewModel
import com.example.todo.viewmodels.TodosViewModelFactory
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.Modifier
import com.example.todo.screens.AddDogScreen
import com.example.todo.viewmodels.DogViewModel
import com.example.todo.viewmodels.DogViewModelFactory



class MainActivity : ComponentActivity() {
    private val todosViewModel: TodosViewModel by viewModels {
        TodosViewModelFactory(
            (application as TodoApplication).todoDAO,
            (application as TodoApplication).dogDAO,
            (application as TodoApplication).tagDAO
        )
    }
    private val dogViewmodel: DogViewModel by viewModels {
        DogViewModelFactory(
            (application as TodoApplication).dogDAO
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(todosViewModel, dogViewmodel)
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(todosViewModel: TodosViewModel, dogViewModel: DogViewModel) {
    val navController = rememberNavController()
    var showTodoForm by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { navController.navigate("history") }) {
                    Icon(Icons.Filled.History, contentDescription = "Completed Tasks")
                }
                IconButton(onClick = { navController.navigate("tasks") }) {
                    Icon(Icons.Filled.Checklist, contentDescription = "Task List")
                }
                IconButton(onClick = { navController.navigate("createDogScreen") }) {
                    Icon(Icons.Filled.Pets, contentDescription = "Add Dog")
                }
            }
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(onClick = {
                    showTodoForm = true
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Task")
                }
            }
        },
    ) { paddingValues ->
        if (showTodoForm) {
            TodoForm(todosViewModel) {
                showTodoForm = false
            }
        } else {
            NavHost(navController, startDestination = "tasks", modifier = Modifier.padding(paddingValues)) {
                composable("tasks") { ActiveTasksScreen(todosViewModel) }
                composable("history") {
                    // Force UI refresh when navigating to the "history" screen
                    LaunchedEffect(Unit) {
                        todosViewModel.loadCompletedTasks()
                    }
                    CompletedTasksHistoryScreen(todosViewModel)
                }
                composable("createDogScreen") {
                    AddDogScreen(dogViewModel, navController = navController) { addedDog ->
                        println("New dog added: ${addedDog.name}")
                    }
                }
            }
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
                value = task.title,
                onValueChange = onTaskTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                singleLine = true,
                placeholder = { Text("Enter task here")}
            )

        }


}



