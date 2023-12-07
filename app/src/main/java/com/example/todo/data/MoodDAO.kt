package com.example.todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todo.entities.DogEntity
import com.example.todo.entities.TagEntity
import com.example.todo.entities.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDAO {
    @Query("SELECT * FROM " + TodoEntity.TABLE_NAME + " WHERE isCompleted = 1 ORDER BY id ASC LIMIT 5")
    suspend fun getLastFiveMoods(moodScore: Int)
}