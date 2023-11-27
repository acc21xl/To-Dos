package com.example.todo.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.tailtasks.enums.Priority
import com.example.tailtasks.enums.Status
import java.util.Date

@Entity(
    tableName = TodoEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(entity = TagEntity::class, parentColumns = ["id"], childColumns = ["tagId"]),
        ForeignKey(entity = DogEntity::class, parentColumns = ["id"], childColumns = ["dogId"]),
        ForeignKey(entity = MoodEntity::class, parentColumns = ["id"], childColumns = ["moodId"])
    ]
)
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val tagId: Int,
    val priority: Priority,
    val imageBytes: ByteArray?,
    val latitude: Double?,
    val longitude: Double?,
    val distance: Double?,
    val status: Status,
    val creationDate: Date,
    val isCompleted: Boolean,
    val toCompleteByDate: Date,
    val completionDate: Date?,
    val dogId: Int,
    val moodId: Int,
    val repeat: Boolean,
    val repeatFrequency: Int,
    val deleted: Boolean
) {
    companion object {
        const val TABLE_NAME = "todos"
    }
}

