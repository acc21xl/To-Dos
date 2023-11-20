package com.example.tailtasks.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.example.tailtasks.enums.Priority
import com.example.tailtasks.enums.Status
import java.util.Date

@Entity(
    tableName = Todo.TABLE_NAME,
    foreignKeys = [
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tag_id"]),
        ForeignKey(entity = Dog::class, parentColumns = ["id"], childColumns = ["dog_id"]),
        ForeignKey(entity = Mood::class, parentColumns = ["id"], childColumns = ["mood_id"])
    ]
)
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val tag_id: Long,
    val priority: Priority,
    val image_bytes: ByteArray?,
    val latitude: Double?,
    val longitude: Double?,
    val distance: Double?,
    val status: Status,
    val creation_date: Date,
    val to_complete_by_date: Date,
    val completion_date: Date?,
    val dog_id: Long,
    val mood_id: Long,
    val repeat: Boolean,
    val repeat_frequency: String,
    val deleted: Boolean
) {
    companion object {
        const val TABLE_NAME = "todos"
    }
}
