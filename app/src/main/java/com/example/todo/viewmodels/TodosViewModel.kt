package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.DogDAO
import com.example.todo.data.TodoDAO
import com.example.todo.entities.DogEntity
import com.example.todo.entities.TagEntity
import com.example.todo.entities.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing todo-related data. Responsible for handling operations related to
 * TodoEntity, including loading, updating, and deleting tasks.
 * Also interacts with DogDAO and TagDAO to manage dog and tag data.
 */
class TodosViewModel(
    private val todoDAO: TodoDAO,
    private val dogDAO: DogDAO
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    private val _dog = MutableStateFlow<DogEntity?>(null)
    val dog = _dog.asStateFlow()
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

    /**
     * Loads and updates the list of completed tasks.
     */
    fun loadCompletedTasks() {
        viewModelScope.launch {
            todoDAO.getCompletedTodos().collect { listOfCompletedTodos ->
                _completedTasks.value = listOfCompletedTodos
            }
        }
    }

    /**
     * Loads and updates the list of active tasks
     */
    private fun loadActiveTasks() {
        viewModelScope.launch {
            todoDAO.getActiveTodos().collect { listOfActiveTodos ->
                _activeTasks.value = listOfActiveTodos
            }
        }
    }

    /**
     * Loads and updates the list of all tasks
     */
    private fun loadTasks() {
        viewModelScope.launch {
            todoDAO.getAllTodos().collect { listOfTodos ->
                _tasks.value = listOfTodos
            }
        }
    }

    /**
     * Loads the first dog from the database (Only ever one dog in DB)
     */
    private fun loadDog() {
        viewModelScope.launch {
            val dog = dogDAO.getDog()
            _dog.value = dog
        }
    }

    /**
     * Submits a new todo along with its associated tags to the database.
     * After submission, it refreshes the list of tasks.
     */
    fun submitTodo(
        newTodo: TodoEntity, tags: List<TagEntity>
    ) {
        viewModelScope.launch {
            todoDAO.insertNewTodoWithTags(newTodo, tags)
            loadTasks()
        }
    }

    /**
     * Updates an existing todo along with its associated tags in the database.
     * After updating, it refreshes the list of tasks
     */
    fun updateTodo(
        todo: TodoEntity, tags: List<TagEntity>
    ) {
        viewModelScope.launch {
            todoDAO.updateTodoWithTags(todo, tags)
            loadTasks()
        }
    }

    /**
     * Retrieves tags associated with a specific todo
     */
    fun getTagsForTodo(todoId: Long): Flow<List<TagEntity>> {
        return todoDAO.getTagsForTodo(todoId)
    }

    /**
     * Updates a task in the database
     */
    fun updateTask(task: TodoEntity) {
        viewModelScope.launch {
            todoDAO.update(task)
            loadTasks()
        }
    }

    /**
     * Deletes a task and its associated tags from the database.
     * After deletion, it refreshes the list of tasks
     */
    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            todoDAO.deleteTodoTags(taskId)
            todoDAO.deleteTask(taskId)
            loadTasks()
        }
    }

}

