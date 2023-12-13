package com.example.todo.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todo.enums.Priority
import com.example.todo.enums.Status
import java.util.Date

@Entity(
    tableName = TodoEntity.TABLE_NAME
)
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val priority: Priority,
    val imageBytes: ByteArray?,
    val latitude: Double?,
    val longitude: Double?,
    val status: Status,
    val creationDate: Date,
    val isCompleted: Boolean,
    val toCompleteByDate: Date,
    val completionDate: Date?,
    val dogId: Int,
    val moodScore: Int?,
    val deleted: Boolean
) {
    companion object {
        const val TABLE_NAME = "todos"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TodoEntity

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (priority != other.priority) return false
        if (imageBytes != null) {
            if (other.imageBytes == null) return false
            if (!imageBytes.contentEquals(other.imageBytes)) return false
        } else if (other.imageBytes != null) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (status != other.status) return false
        if (creationDate != other.creationDate) return false
        if (isCompleted != other.isCompleted) return false
        if (toCompleteByDate != other.toCompleteByDate) return false
        if (completionDate != other.completionDate) return false
        if (dogId != other.dogId) return false
        if (moodScore != other.moodScore) return false
        if (deleted != other.deleted) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + priority.hashCode()
        result = 31 * result + (imageBytes?.contentHashCode() ?: 0)
        result = 31 * result + (latitude?.hashCode() ?: 0)
        result = 31 * result + (longitude?.hashCode() ?: 0)
        result = 31 * result + status.hashCode()
        result = 31 * result + creationDate.hashCode()
        result = 31 * result + isCompleted.hashCode()
        result = 31 * result + toCompleteByDate.hashCode()
        result = 31 * result + (completionDate?.hashCode() ?: 0)
        result = 31 * result + dogId
        result = 31 * result + (moodScore ?: 0)
        result = 31 * result + deleted.hashCode()
        return result
    }
}

