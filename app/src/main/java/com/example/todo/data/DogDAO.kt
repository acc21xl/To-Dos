package com.example.todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todo.entities.DogEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface DogDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dog: DogEntity)
    @Update
    suspend fun update(dog: DogEntity)
    @Delete
    suspend fun delete(dog: DogEntity)
    @Query("SELECT * FROM " + DogEntity.TABLE_NAME + " WHERE id = :id")
    fun getDog(id: Int): DogEntity
    @Query("SELECT * FROM " + DogEntity.TABLE_NAME +  " ORDER BY id ASC")
    fun getAllDogs(): Flow<List<DogEntity>>
}