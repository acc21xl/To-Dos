package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.DogDAO
import com.example.todo.entities.DogEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DogViewModel(private val dogDAO: DogDAO) : ViewModel() {
    private val _dog = MutableStateFlow<DogEntity?>(null)
    val dog = _dog.asStateFlow()

    init {
        loadDog()
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
        }
    }

    fun deleteDog(dog: DogEntity) {
        viewModelScope.launch {
            dogDAO.delete(dog)
        }
    }

}
