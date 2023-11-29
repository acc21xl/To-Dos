package com.example.todo.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.todo.entities.TagEntity
import com.example.todo.entities.TodoEntity
import com.example.todo.entities.TodoTagJoin
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: TodoEntity): Long
    @Update
    suspend fun update(todo: TodoEntity)
    @Delete
    suspend fun delete(todo: TodoEntity)

    @Query("SELECT * FROM todos ORDER BY id ASC")
    fun getAllTodos(): Flow<List<TodoEntity>>
    @Query("SELECT * FROM " + TodoEntity.TABLE_NAME + " WHERE id = :id")
    fun getTodo(id: Int): TodoEntity
    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY id ASC")
    fun getActiveTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY id ASC")
    fun getCompletedTodos(): Flow<List<TodoEntity>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodoTagJoin(todoTagJoin: TodoTagJoin)

    @Query("SELECT * FROM tags WHERE title = :name")
    suspend fun getTagByName(name: String): TagEntity?

//    @Query("SELECT toCompleteByDate FROM todos")
//    fun getDateToCompleter() : Flow<List<TodoEntity>>

    @Transaction
    suspend fun insertNewTodoWithTags(newTodo: TodoEntity, tags: List<TagEntity>) {
        val newTodoId = insert(newTodo)
        tags.forEach { tag ->
            val tagId = getTagByName(tag.title)?.id ?: insertTag(tag)
            insertTodoTagJoin(TodoTagJoin(newTodoId, tagId.toLong()))
        }
    }


}