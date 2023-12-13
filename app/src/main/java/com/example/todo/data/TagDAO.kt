package com.example.todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todo.entities.TagEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Tags
 * This interface define the database operations related to Tags like insert, update, delete,
 * and queries.
 */
@Dao
interface TagDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: TagEntity)
    @Update
    suspend fun update(tag: TagEntity)
    @Delete
    suspend fun delete(tag: TagEntity)
    @Query("SELECT * FROM " + TagEntity.TABLE_NAME + " WHERE id = :id")
    fun getTag(id: Int): TagEntity
    @Query("SELECT * FROM " + TagEntity.TABLE_NAME +  " ORDER BY id ASC")
    fun getAllTags(): Flow<List<TagEntity>>
}