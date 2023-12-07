package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.DogDAO
import com.example.todo.data.TagDAO
import com.example.todo.data.TodoDAO
import com.example.todo.entities.DogEntity
import com.example.todo.entities.TagEntity
import com.example.todo.entities.TodoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MoodViewModel(private val todoDAO: TodoDAO, private val dogDAO: DogDAO,
                    private val tagDAO: TagDAO
) : ViewModel(){

    private val _tasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val dog = MutableStateFlow<DogEntity?>(null)
    val tasks = _tasks.asStateFlow()
    private val _activeTasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val activeTasks = _activeTasks.asStateFlow()

    // Completed tasks
    private val _completedTasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val completedTasks = _completedTasks.asStateFlow()
    init {
        loadCompletedTasks()
        loadDog()
    }

    private fun loadCompletedTasks() {
        viewModelScope.launch {
            todoDAO.getCompletedTodos().collect { listOfCompletedTodos ->
                _completedTasks.value = listOfCompletedTodos
            }
        }
    }


    private fun loadDog() {
        viewModelScope.launch {
            val dogs = dogDAO.getAllDogs().first()
            dog.value = dogs.firstOrNull()
        }
    }

}