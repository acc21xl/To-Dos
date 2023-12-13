package com.example.todo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
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
import com.example.todo.screens.ActiveTodos
import com.example.todo.screens.TodoHistory
import com.example.todo.screens.TodoForm
import com.example.todo.ui.theme.TodoTheme
import com.example.todo.viewmodels.TodosViewModel
import com.example.todo.viewmodels.TodosViewModelFactory
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.Modifier
import com.example.todo.screens.DogForm
import com.example.todo.screens.ViewDog
import com.example.todo.viewmodels.DogViewModel
import com.example.todo.viewmodels.DogViewModelFactory
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.screens.TypicalTodos
import com.example.todo.services.GeoLocationService
import com.example.todo.viewmodels.LocationViewModel
import com.example.todo.entities.DogEntity

class MainActivity : ComponentActivity() {
    private val todosViewModel: TodosViewModel by viewModels {
        TodosViewModelFactory(
            (application as TodoApplication).todoDAO,
            (application as TodoApplication).dogDAO,
            (application as TodoApplication).tagDAO
        )
    }
    private val dogViewModel: DogViewModel by viewModels {
        DogViewModelFactory(
            (application as TodoApplication).dogDAO,
            (application as TodoApplication).todoDAO
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        GeoLocationService.initialiseService(applicationContext)
        if (!hasPermission()) {
            requestFineLocationPermission()
        }
        setContent {
            val locationViewModel = viewModel<LocationViewModel>()
            GeoLocationService.locationViewModel = locationViewModel
            TodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    // Display the appropriate screen based on the presence of dogs
                    val navController = rememberNavController()
                    val dog by dogViewModel.dog.collectAsState()

                    if (dog == null) {
                        DogForm(
                            viewModel = dogViewModel,
                            navController = navController,
                            onDogAdded = { addedDog: DogEntity ->
                                println("Added dog: ${addedDog.name}")
                            }
                        )
                    } else {
                        MainScreen(todosViewModel, dogViewModel)
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        @SuppressLint("MissingPermission")
        if (hasPermission()) {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                GeoLocationService.updateLatestLocation(location)
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000, 0.0f, GeoLocationService
            )
        }
    }

    override fun onPause() {
        super.onPause()
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(GeoLocationService)
    }

    private val GPS_LOCATION_PERMISSION_REQUEST = 1
    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION ),
            GPS_LOCATION_PERMISSION_REQUEST
        )
    }

    private fun hasPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(
                    applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION )
    }
}

object Routes {
    const val DOG_DETAILS = "dogDetails"
    const val CREATE_DOG_SCREEN = "createDogScreen"
    const val TASKS = "tasks"
    const val TYPICAL_TASKS = "typicaltasks"
    const val HISTORY = "history"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(todosViewModel: TodosViewModel, dogViewModel: DogViewModel) {
    val navController = rememberNavController()
    var showTodoForm by remember { mutableStateOf(false) }
    val moodScoresState = dogViewModel.recentMoodScores.collectAsState()
    val moodScores = moodScoresState.value
    val moodScore = if (moodScores.isNotEmpty()) {
        moodScores.average()
    } else {
        3.0
    }
    val moodText = dogViewModel.getMoodText(moodScore)
    val moodColor = dogViewModel.getMoodColor(moodScore)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Dog Is: $moodText") },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = moodColor)
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (dogViewModel.dog.value != null) {
                            // Navigate to DogDetailsScreen if there is a dog
                            navController.navigate(Routes.DOG_DETAILS)
                        } else {
                            // Navigate to createDogScreen if there is no dog
                            navController.navigate(Routes.CREATE_DOG_SCREEN)
                        }
                    }) {
                        Icon(Icons.Filled.Pets, contentDescription = "Dog")
                    }
                    IconButton(onClick = { navController.navigate(Routes.TASKS) }) {
                        Icon(Icons.Filled.Checklist, contentDescription = "Task List")
                    }
                    IconButton(onClick = { navController.navigate(Routes.TYPICAL_TASKS) }) {
                        Icon(Icons.Filled.Repeat, contentDescription = "Typical Tasks")
                    }
                    IconButton(onClick = { navController.navigate(Routes.HISTORY) }) {
                        Icon(Icons.Filled.History, contentDescription = "Completed Tasks")
                    }
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
            TodoForm(
                viewModel = todosViewModel,
                onClose = { showTodoForm = false }
            )
        } else {
            NavHost(navController, startDestination = "tasks",
                modifier = Modifier.padding(paddingValues)) {
                composable("tasks") { ActiveTodos(todosViewModel) }
                composable("history") {
                    // Force UI refresh when navigating to the "history" screen
                    LaunchedEffect(Unit) {
                        todosViewModel.loadCompletedTasks()
                    }
                    TodoHistory(todosViewModel)
                }
                composable("createDogScreen") {
                    DogForm(
                        viewModel = dogViewModel,
                        navController = navController,
                        onDogAdded = { addedDog: DogEntity ->
                            println("Added dog: ${addedDog.name}")
                        }
                    )

                }
                composable("dogDetails") {
                    ViewDog(navController, dogViewModel)
                }
                composable("typicaltasks") {
                    TypicalTodos(todosViewModel)
                }
            }
        }
    }
}

@Composable
fun TaskRow(
    task: TodoEntity,
    onTaskCheckedChange: (Boolean) -> Unit,
    onTaskClicked: () -> Unit,
    onVisibilityClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onTaskCheckedChange
            )
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            IconButton(onClick = onTaskClicked) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onVisibilityClicked) {
                Icon(imageVector = Icons.Default.Visibility, contentDescription = "View")
            }
            IconButton(
                onClick = onDeleteClicked,
                modifier = Modifier.padding(start = 8.dp),
                content = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
            }
        }
    }






