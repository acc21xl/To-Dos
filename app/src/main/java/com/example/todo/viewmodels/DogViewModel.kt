package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.DogDAO
import com.example.todo.data.TodoDAO
import com.example.todo.entities.DogEntity
import com.example.todo.entities.TodoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DogViewModel(private val dogDAO: DogDAO,
                   private val todoDAO: TodoDAO) : ViewModel() {
    private val _dog = MutableStateFlow<DogEntity?>(null)
    val dog = _dog.asStateFlow()
    private val _completedTasks = MutableStateFlow<List<TodoEntity>>(emptyList())
    val completedTasks = _completedTasks.asStateFlow()
    val _recentMoodScores = MutableStateFlow<List<Int>>(emptyList())
    val recentMoodScores = _recentMoodScores.asStateFlow()

    init {
        loadDog()
        loadCompletedTasks()
    }

    private fun loadDog() {
        viewModelScope.launch {
            val dogs = dogDAO.getAllDogs().first()
            _dog.value = dogs.firstOrNull()
        }
    }

    fun createDog(newDogEntity: DogEntity) {
        viewModelScope.launch {
            val result = dogDAO.insert(newDogEntity)
            loadDog()
        }
    }


    fun updateDog(dog: DogEntity) {
        viewModelScope.launch {
            dogDAO.update(dog)
            loadDog()
        }
    }

    fun deleteDog(dog: DogEntity) {
        viewModelScope.launch {
            dogDAO.delete(dog)
        }
    }
    suspend fun getDog(): DogEntity? {
        return dogDAO.getDog()
    }
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

    private fun loadCompletedTasks() {
        viewModelScope.launch {
            todoDAO.getCompletedTodos().collect { listOfCompletedTodos ->
                _completedTasks.value = listOfCompletedTodos
                updateRecentMoodScores(listOfCompletedTodos)
            }
        }
    }

    private fun updateRecentMoodScores(completedTodos: List<TodoEntity>) {
        val moodScores = completedTodos
            .filter { it.moodScore != null }
            .sortedByDescending { it.completionDate ?: it.creationDate }
            .take(5)
            .mapNotNull { it.moodScore }

        _recentMoodScores.value = moodScores
    }
}
