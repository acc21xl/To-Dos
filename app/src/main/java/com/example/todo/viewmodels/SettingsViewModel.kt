package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.TodoDAO
import com.example.todo.entities.TodoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val todoDAO: TodoDAO) : ViewModel() {
    private val _tasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val tasks = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            todoDAO.getAllTodos().collect { listOfTodos ->
                _tasks.value = listOfTodos
        }
    }
}

    fun addTask(taskText: String) {
        viewModelScope.launch {
            val newTask = TodoEntity(text = taskText, isCompleted = false)
            todoDAO.insert(newTask)
            loadTasks() // Reload the tasks to update the UI
        }
    }

    fun updateTask(task: TodoEntity) {
        viewModelScope.launch {
            todoDAO.update(task)
            loadTasks() // Reload the tasks to update the UI
        }
    }
}

