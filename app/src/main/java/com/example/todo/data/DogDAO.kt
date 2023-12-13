package com.example.todo.data

import androidx.room.*
import com.example.todo.entities.DogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Dogs
 * This interface defines the database operations related to Dogs like insert, update, delete,
 * and queries
 */
@Dao
interface DogDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dog: DogEntity): Long

    @Update
    suspend fun update(dog: DogEntity)

    @Delete
    suspend fun delete(dog: DogEntity)

    @Query("SELECT * FROM " + DogEntity.TABLE_NAME + " LIMIT 1")
    suspend fun getDog(): DogEntity?

    @Query("SELECT * FROM " + DogEntity.TABLE_NAME + " ORDER BY id ASC")
    fun getAllDogs(): Flow<List<DogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(dog: DogEntity)
}
