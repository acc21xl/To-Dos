package com.example.tailtasks.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tailtasks.model.Todo

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(todo: Todo)
    @Update
    suspend fun update(todo: Todo)
    @Delete
    suspend fun delete(todo: Todo)
    @Query("SELECT * FROM " + Todo.TABLE_NAME + " WHERE id = :id")
    fun getTodo(id: Int): Todo
    @Query("SELECT * FROM " + Todo.TABLE_NAME +  " ORDER BY id ASC")
    fun getAllTodos(): List<Todo>
}

