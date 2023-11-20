package com.example.tailtasks.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tailtasks.model.Mood

@Dao
interface MoodDao {
    @Insert
    suspend fun insert(mood: Mood)
    @Update
    suspend fun update(mood: Mood)
    @Delete
    suspend fun delete(mood: Mood)
    @Query("SELECT * FROM " + Mood.TABLE_NAME + " WHERE id = :id")
    fun getMood(id: Int): Mood
    @Query("SELECT * FROM " + Mood.TABLE_NAME +  " ORDER BY id ASC")
    fun getAllMoods(): List<Mood>
}