package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo.data.DogDAO
import com.example.todo.data.TodoDAO

class DogViewModelFactory(private val dogDAO: DogDAO, private val todoDAO: TodoDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DogViewModel(dogDAO, todoDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

