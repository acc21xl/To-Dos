package com.example.tailtasks.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tailtasks.model.Tag

@Dao
interface TagDao {
    @Insert
    suspend fun insert(tag: Tag)
    @Update
    suspend fun update(tag: Tag)
    @Delete
    suspend fun delete(tag: Tag)
    @Query("SELECT * FROM " + Tag.TABLE_NAME + " WHERE id = :id")
    fun getTag(id: Int): Tag
    @Query("SELECT * FROM " + Tag.TABLE_NAME +  " ORDER BY id ASC")
    fun getAllTags(): List<Tag>
}