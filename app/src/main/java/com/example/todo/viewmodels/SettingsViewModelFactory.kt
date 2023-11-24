package com.example.todo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo.data.TodoDAO

class SettingsViewModelFactory(private val todoDAO: TodoDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(todoDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

