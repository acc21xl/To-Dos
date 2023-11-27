package com.example.todo.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.tailtasks.enums.MoodScore

@Entity(
    tableName = MoodEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(entity = TodoEntity::class, parentColumns = ["id"], childColumns = ["todoId"])
    ]
)
data class MoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val todoId: Int,
    val score: MoodScore,
    val notes: String
){
    companion object {
        const val TABLE_NAME = "moods"
    }
}
