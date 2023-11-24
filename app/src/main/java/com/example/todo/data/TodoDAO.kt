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
    suspend fun insert(todo: TodoEntity)
    @Update
    suspend fun update(todo: TodoEntity)
    @Delete
    suspend fun delete(todo: TodoEntity)

    @Query("SELECT * FROM todos ORDER BY id ASC")
    fun getAllTodos(): Flow<List<TodoEntity>>
}