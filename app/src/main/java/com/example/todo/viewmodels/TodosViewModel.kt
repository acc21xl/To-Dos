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

class TodosViewModel(private val todoDAO: TodoDAO, private val dogDAO: DogDAO,
                     private val tagDAO: TagDAO) : ViewModel() {
    private val _tasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val dog = MutableStateFlow<DogEntity?>(null)
    val tasks = _tasks.asStateFlow()
    private val _activeTasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val activeTasks = _activeTasks.asStateFlow()

    // Completed tasks
    private val _completedTasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val completedTasks = _completedTasks.asStateFlow()
    init {
        loadTasks()
        loadActiveTasks()
        loadDog()
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

    private fun loadTasks() {
        viewModelScope.launch {
            todoDAO.getAllTodos().collect { listOfTodos ->
                _tasks.value = listOfTodos
            }
        }
    }

    private fun loadDog() {
        viewModelScope.launch {
            val dogs = dogDAO.getAllDogs().first()
            dog.value = dogs.firstOrNull()
        }
    }
    fun submitTodo(
        newTodo: TodoEntity, tags: List<TagEntity>
    ) {
        viewModelScope.launch {
            val newTodoId = todoDAO.insertNewTodoWithTags(newTodo, tags)
            loadTasks()
        }
    }

    fun updateTask(task: TodoEntity) {
        viewModelScope.launch {
            todoDAO.update(task)
            loadTasks()
        }
    }

//    class TodoRepository(private val todoDAO: TodoDAO) {
//        fun getNextTodoTime(): Flow<List<TodoEntity>> {
//            val todos = todoDAO.getAllTodos()
//            val dates = todoDAO.getDateToCompleter()
//            val currentTime = System.currentTimeMillis()
//            return dates
//
//        }
//    }

}

