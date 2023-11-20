package com.example.tailtasks.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tailtasks.model.Dog

@Dao
interface DogDao {
    @Insert
    suspend fun insert(dog: Dog)
    @Update
    suspend fun update(dog: Dog)
    @Delete
    suspend fun delete(dog: Dog)
    @Query("SELECT * FROM " + Dog.TABLE_NAME + " WHERE id = :id")
    fun getDog(id: Int): Dog
    @Query("SELECT * FROM " + Dog.TABLE_NAME +  " ORDER BY id ASC")
    fun getAllTodos(): List<Dog>
}