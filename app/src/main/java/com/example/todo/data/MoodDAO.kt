package com.example.todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todo.entities.MoodEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface MoodDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(mood: MoodEntity): Long
    @Update
    suspend fun update(mood: MoodEntity)
    @Delete
    suspend fun delete(mood: MoodEntity)
    @Query("SELECT * FROM " + MoodEntity.TABLE_NAME + " WHERE id = :id")
    fun getMood(id: Int): MoodEntity
    @Query("SELECT * FROM " + MoodEntity.TABLE_NAME +  " ORDER BY id ASC")
    fun getAllMoods(): Flow<List<MoodEntity>>
}