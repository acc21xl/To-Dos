package com.example.tailtasks.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "dogs")
data class Dog(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val image_bytes: ByteArray?,
    val breed: String,
    val age: Int,
    val birthday_date: Date,
    val notes: String,
    val current_mood: Float, // Calculated property, not stored in DB
    val deleted: Boolean
)
