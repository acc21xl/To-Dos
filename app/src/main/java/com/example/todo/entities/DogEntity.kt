package com.example.todo.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = DogEntity.TABLE_NAME)
data class DogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val imageBytes: ByteArray?,
    val breed: String,
    val birthdayDate: Date,
    val notes: String,
    val deleted: Boolean
){
    companion object {
        const val TABLE_NAME = "dogs"
    }
}
