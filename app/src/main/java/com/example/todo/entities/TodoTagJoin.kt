package com.example.todo.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entity class representing the many-to-many relationship between TodoEntity and TagEntity.
 * This class maps to a table in the database that serves as a join table between todos and tags,
 * allowing the association of multiple tags with a single todo item and vice versa.
 *
 * The table consists of two primary keys, 'todoId' and 'tagId', which are foreign keys referencing
 * the primary keys of the TodoEntity and TagEntity tables
 */

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