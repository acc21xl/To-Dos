package com.example.tailtasks.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.tailtasks.enums.MoodScore

@Entity(
    tableName = "moods",
    foreignKeys = [
        ForeignKey(entity = Todo::class, parentColumns = ["id"], childColumns = ["todo_id"])
    ]
)
data class Mood(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val todo_id: Int,
    val score: MoodScore,
    val notes: String
)
