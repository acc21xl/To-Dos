package com.example.todo.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = DogEntity.TABLE_NAME)
data class DogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val imageBytes: ByteArray?,
    val breed: String,
    val age: Int,
    val birthdayDate: Date,
    val notes: String,
    val currentMood: Float, // Calculated property, not stored in DB
    val deleted: Boolean
){
    companion object {
        const val TABLE_NAME = "dogs"
    }
}
