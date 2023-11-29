package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo.data.DogDAO

class DogViewModelFactory(private val dogDAO: DogDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DogViewModel(dogDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

