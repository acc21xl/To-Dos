package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tailtasks.enums.MoodScore
import com.example.tailtasks.enums.Priority
import com.example.tailtasks.enums.Status
import com.example.todo.data.DogDAO
import com.example.todo.data.MoodDAO
import com.example.todo.data.TagDAO
import com.example.todo.data.TodoDAO
import com.example.todo.entities.DogEntity
import com.example.todo.entities.MoodEntity
import com.example.todo.entities.TagEntity
import com.example.todo.entities.TodoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class TodosViewModel(private val todoDAO: TodoDAO, private val dogDAO: DogDAO,
                     private val tagDAO: TagDAO, private val moodDAO: MoodDAO) : ViewModel() {
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
        loadDog()
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
        title: String, description: String, tags: List<TagEntity>, mood: MoodScore,
        priority: Priority, status: Status, repeat: Boolean, repeatFrequency: Int,
        latitude: Double, longitude: Double, distance: Double, isCompleted: Boolean,
        toCompleteByDate: Date, creationDate: Date, completionDate: Date?
    ) {
        viewModelScope.launch {
            fun submitTodo(
                title: String, description: String, tags: List<TagEntity>, mood: MoodScore,
                priority: Priority, status: Status, repeat: Boolean, repeatFrequency: Int,
                latitude: Double, longitude: Double, distance: Double, isCompleted: Boolean,
                toCompleteByDate: Date, creationDate: Date, completionDate: Date?
            ) {
                viewModelScope.launch {
                    val newTodo = TodoEntity(
                        title = title,
                        description = description,
                        tagId = 0,
                        dogId = dog.value?.id ?: 0,
                        moodId = 0,
                        priority = priority,
                        status = status,
                        repeat = repeat,
                        repeatFrequency = repeatFrequency,
                        latitude = latitude,
                        longitude = longitude,
                        distance = distance,
                        isCompleted = isCompleted,
                        toCompleteByDate = toCompleteByDate,
                        creationDate = creationDate,
                        completionDate = completionDate,
                        deleted = false,
                        imageBytes = null
                    )
                    val newTodoId = todoDAO.insert(newTodo)

                    val moodEntity = MoodEntity(
                        todoId = newTodoId.toInt(), score = mood,
                        notes = "", id = 0
                    )
                    moodDAO.insert(moodEntity)
                    loadTasks()
                }
            }

            fun addTag(tagTitle: String, onTagAdded: (TagEntity) -> Unit) {
                viewModelScope.launch {
                    val newTag = TagEntity(title = tagTitle, id = 0)
                    onTagAdded(newTag.copy(id = 0))
                }
            }

            fun updateTask(task: TodoEntity) {
                viewModelScope.launch {
                    todoDAO.update(task)
                    loadTasks()
                }
            }
        }
    }
}

