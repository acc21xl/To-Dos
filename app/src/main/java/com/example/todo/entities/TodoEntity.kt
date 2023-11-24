package com.example.todo.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TodoEntity.TABLE_NAME)
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val isCompleted: Boolean
) {
    companion object {
        const val TABLE_NAME = "todos"
    }
}

