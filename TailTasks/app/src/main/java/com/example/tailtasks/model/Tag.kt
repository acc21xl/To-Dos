package com.example.tailtasks.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Tag.TABLE_NAME)
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String
){
    companion object {
        const val TABLE_NAME = "tags"
    }
}
