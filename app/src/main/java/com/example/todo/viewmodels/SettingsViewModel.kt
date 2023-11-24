package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.TodoDAO
import com.example.todo.entities.TodoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val todoDAO: TodoDAO) : ViewModel() {
    // Active tasks
    private val _activeTasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val activeTasks = _activeTasks.asStateFlow()

    // Completed tasks
    private val _completedTasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val completedTasks = _completedTasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            todoDAO.getAllTodos().collect { listOfTodos ->
                _activeTasks.value = listOfTodos.filter { !it.isCompleted }
                _completedTasks.value = listOfTodos.filter { it.isCompleted }
            }
        }
    }

    fun addTask(taskText: String) {
        viewModelScope.launch {
            val newTask = TodoEntity(text = taskText, isCompleted = false)
            todoDAO.insert(newTask)
        }
    }

    fun updateTask(task: TodoEntity) {
        viewModelScope.launch {
            todoDAO.update(task)
            // Ensure the below method is called to refresh the list
            loadTasks()
        }
    }

    fun loadCompletedTasks() {
        viewModelScope.launch {
            todoDAO.getCompletedTodos().collect { listOfCompletedTodos ->
                _completedTasks.value = listOfCompletedTodos
            }
        }
    }

    fun loadActiveTasks() {
        viewModelScope.launch {
            todoDAO.getActiveTodos().collect { listOfActiveTodos ->
                _activeTasks.value = listOfActiveTodos
            }
        }
    }


}

