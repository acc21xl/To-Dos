package com.example.todo.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "todo_tag_join",
    primaryKeys = ["todoId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = TodoEntity::class,
            parentColumns = ["id"],
            childColumns = ["todoId"]),

        ForeignKey(entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"])
    ],
    indices = [Index(value = ["todoId"]), Index(value = ["tagId"])]
)
data class TodoTagJoin(
    val todoId: Long,
    val tagId: Long
)