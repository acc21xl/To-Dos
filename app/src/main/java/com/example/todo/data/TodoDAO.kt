package com.example.todo.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todo.entities.TodoEntity
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
}