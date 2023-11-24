package com.example.todo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.data.TodoDAO
import com.example.todo.data.TodoDatabase
import com.example.todo.entities.TodoEntity
import com.example.todo.ui.theme.TodoTheme
import com.example.todo.viewmodels.SettingsViewModel
import com.example.todo.viewmodels.SettingsViewModelFactory
import kotlinx.coroutines.launch


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

                    // Pass the viewModel instance to ScreenScaffold
                    ScreenScaffold(settingsViewModel)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(settingsViewModel: SettingsViewModel) {
    // State for new task text input, which you can remove as it's no longer needed here
    // var newTaskText by rememberSaveable { mutableStateOf("") }

    // This will represent the current list of tasks as a StateList
    val tasks = settingsViewModel.tasks.collectAsState().value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // This will add a new task with default values at the end of the list
                settingsViewModel.addTask("")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            tasks.forEach { task ->
                TaskRow(task, onTaskCheckedChange = { isChecked ->
                    settingsViewModel.updateTask(task.copy(isCompleted = isChecked))
                }, onTaskTextChange = { updatedText ->
                    // Update the text for an existing task when the user finishes editing the text field
                    settingsViewModel.updateTask(task.copy(text = updatedText))
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskRow(
    task: TodoEntity,
    onTaskCheckedChange: (Boolean) -> Unit,
    onTaskTextChange: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { isChecked ->
                onTaskCheckedChange(isChecked)
            }
        )
        TextField(
            value = task.text,
            onValueChange = { updatedText ->
                onTaskTextChange(updatedText)
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            placeholder = { Text("Enter task here") }
        )
    }
}


//@Composable
//fun SettingsContent(padding: PaddingValues,
//                    viewModel: SettingsViewModel
//){
//    Row(modifier = Modifier
//        .fillMaxWidth()
//        .padding(padding)
//        .padding(start = 10.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically)
//    {
//        Row {
//            Text("Invert Keys")
//            KeyText("text",viewModel.invertedKeys == true)
//        }
//        Checkbox(checked = viewModel.invertedKeys,
//            onCheckedChange = {viewModel.updateInvertedKeys(it)} )
//    }
//}
//@Composable
//fun SaveButton(padding: PaddingValues) {
//    val buttonScope = rememberCoroutineScope()
//    val dao = TodoDatabase.getDB(LocalContext.current).todoDao()
//    fun insertOnClick() {
//        buttonScope.launch {
//            dao.insert(
//                TodoEntity(
//                    description = "description",
//                    keyList = "Alt Shift Keys",
//                    applicationName = "Test",
//                    numericOrder = 1
//                )
//            )
//        }
//    }
//    Row(modifier = Modifier
//        .fillMaxWidth()
//        .padding(padding)
//
//    ) {
//        Text("Text")
//        Button(onClick = ::insertOnClick ) {
//            Text("Add example data")
//        }
//    }
//}
//@Composable
//fun KeyText(text: String, inverted: Boolean = false) {
//    if (inverted) {
//        Text(text, modifier = Modifier.background(Color.Black),
//            color = Color.White)
//    } else {
//        Text(text)
//    }
//}
//val todoDao = TodoDatabase.getInstance(context).todoDao()
//
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    TodoTheme {
//        ScreenScaffold(SettingsViewModel(TodoDAO())) // You need to implement a FakeTodoDao for preview purposes
//    }
//}
