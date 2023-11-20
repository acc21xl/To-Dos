package com.example.tailtasks.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tailtasks.dao.DogDao
import com.example.tailtasks.dao.MoodDao
import com.example.tailtasks.dao.TagDao
import com.example.tailtasks.dao.TodoDao
import com.example.tailtasks.model.Dog
import com.example.tailtasks.model.Mood
import com.example.tailtasks.model.Tag
import com.example.tailtasks.model.Todo

@Database(entities = [Todo::class, Dog::class, Tag::class, Mood::class],
    version = 1,
    exportSchema = false)
abstract class TailTasksDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun dogDao(): DogDao
    abstract fun moodDao(): MoodDao
    abstract fun tagDao(): TagDao
    companion object {
        private const val DB_NAME = "tail_tasks_db"
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context,
                TailTasksDatabase::class.java,
                DB_NAME ).fallbackToDestructiveMigration().build()

        @Volatile private var thisDB: TailTasksDatabase? = null
        fun getDB(context: Context): TailTasksDatabase =
            thisDB ?: synchronized(this) {
                thisDB ?: buildDatabase(context).also { thisDB = it }
            }
    }
}