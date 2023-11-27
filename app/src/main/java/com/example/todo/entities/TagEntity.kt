package com.example.todo.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TagEntity.TABLE_NAME)
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String
){
    companion object {
        const val TABLE_NAME = "tags"
    }
}
