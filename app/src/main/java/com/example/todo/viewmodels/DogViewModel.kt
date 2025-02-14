package com.example.todo.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.DogDAO
import com.example.todo.data.TodoDAO
import com.example.todo.entities.DogEntity
import com.example.todo.entities.TodoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * This ViewModel handles operations related to the Dog, such as creating, updating,
 * and deleting dogs (There is ever only one)
 * It also manages the retrieval of mood scores based on completed tasks and provides utility
 * methods for converting these mood scores into color and text representations
 */
class DogViewModel(private val dogDAO: DogDAO,
                   private val todoDAO: TodoDAO) : ViewModel() {
    private val _dog = MutableStateFlow<DogEntity?>(null)
    val dog = _dog.asStateFlow()
    private val _completedTasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val completedTasks = _completedTasks.asStateFlow()
    private val _recentMoodScores = MutableStateFlow<List<Int>>(emptyList())
    val recentMoodScores = _recentMoodScores.asStateFlow()

    init {
        loadDog()
        loadCompletedTasks()
    }

    fun loadDog() {
        viewModelScope.launch {
            val dog = dogDAO.getDog()
            _dog.value = dog
        }
    }

    fun createDog(newDogEntity: DogEntity) {
        viewModelScope.launch {
            dogDAO.insert(newDogEntity)
            loadDog()
        }
    }

    fun updateDog(dog: DogEntity) {
        viewModelScope.launch {
            dogDAO.update(dog)
            loadDog()
        }
    }

    /**
     * Loads the dog information from the database.
     */
    fun deleteDog(dog: DogEntity) {
        viewModelScope.launch {
            dogDAO.delete(dog)
        }
    }

    /**
     * Either creates or updates and existing dog depending on if one exists or not
     */
    fun createOrUpdateDog(newDogEntity: DogEntity) {
        viewModelScope.launch {
            val existingDog = dogDAO.getDog()
            val dogToUpdate = existingDog?.copy(
                name = newDogEntity.name,
                breed = newDogEntity.breed,
                birthdayDate = newDogEntity.birthdayDate,
                notes = newDogEntity.notes,
                imageBytes = newDogEntity.imageBytes,
                deleted = false
            ) ?: newDogEntity

            dogDAO.insertOrUpdate(dogToUpdate)
            loadDog()
        }

    }

    /**
     * Loads completed tasks from the database and updates the recent mood scores
     */
    fun loadCompletedTasks() {
        viewModelScope.launch {
            todoDAO.getCompletedTodos().collect { listOfCompletedTodos ->
                _completedTasks.value = listOfCompletedTodos
                updateRecentMoodScores(listOfCompletedTodos)
            }
        }
    }

    /**
     * Updates the recent mood scores based on the latest completed tasks
     */
    fun updateRecentMoodScores(completedTodos: List<TodoEntity>) {
        val moodScores = completedTodos
            .filter { it.moodScore != null }
            .sortedByDescending { it.completionDate ?: it.creationDate }
            .take(5)
            .mapNotNull { it.moodScore }

        _recentMoodScores.value = moodScores
    }

    /**
     * Converts an average mood score into a corresponding color representation
     */
    fun getMoodColor(averageMood: Double): Color {
        val moodColors = listOf(
            Color.Red, // 1 - Very Unhappy
            Color(0xFFFFA500), // 2 - Unhappy
            Color(0xFFFFA500), // 3 - Neutral
            Color(0xFF90EE90), // 4 - Happy
            Color(0xFF006400) // 5 - Very Happy
        )

        val roundedMood = averageMood.roundToInt().coerceIn(1, 5)
        return moodColors[roundedMood - 1]
    }

    /**
     * Converts an average mood score into a corresponding text representation
     */
    fun getMoodText(averageMood: Double): String {

        return when (averageMood.roundToInt().coerceIn(1, 5)) {
            1 -> "Very Unhappy"
            2 -> "Unhappy"
            3 -> "Neutral"
            4 -> "Happy"
            else -> "Very Happy"
        }
    }
}
