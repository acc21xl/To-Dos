package com.example.todo.data

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

/**
 * Data Access Object for the Todos
 * This interface defines the database operations related to Todos like insert, update, delete,
 * and queries
 * Contains some custom queries for inserting and deleting Todos with foreign key constraints
 */
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

    @Transaction
    suspend fun insertNewTodoWithTags(newTodo: TodoEntity, tags: List<TagEntity>) {
        val newTodoId = insert(newTodo)
        tags.forEach { tag ->
            val tagId = getTagByName(tag.title)?.id ?: insertTag(tag)
            insertTodoTagJoin(TodoTagJoin(newTodoId, tagId.toLong()))
        }
    }

    @Transaction
    suspend fun updateTodoWithTags(todo: TodoEntity, tags: List<TagEntity>) {
        update(todo)

        deleteTodoTags(todo.id.toLong())

        tags.forEach { tag ->
            val tagId = getTagByName(tag.title)?.id ?: insertTag(tag)
            insertTodoTagJoin(TodoTagJoin(todo.id.toLong(), tagId.toLong()))
        }
    }

    @Query("SELECT tags.* FROM tags JOIN todo_tag_join ON tags.id = todo_tag_join.tagId WHERE todo_tag_join.todoId = :todoId")
    fun getTagsForTodo(todoId: Long): Flow<List<TagEntity>>

    @Query("DELETE FROM todo_tag_join WHERE todoId = :todoId")
    suspend fun deleteTodoTags(todoId: Long)
    @Query("DELETE FROM todos WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)
}