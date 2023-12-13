package com.example.todo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todo.converters.Converters
import com.example.todo.entities.DogEntity
import com.example.todo.entities.TagEntity
import com.example.todo.entities.TodoEntity
import com.example.todo.entities.TodoTagJoin

/**
 * Main database setup for the application.
 * This abstract class extends RoomDatabase and declares all the DAOs and entities used in the app
 * It also includes methods to build the database instance
 */

@Database(entities = [TodoEntity::class, DogEntity::class, TagEntity::class, TodoTagJoin::class], version = 10, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDAO(): TodoDAO
    abstract fun dogDAO(): DogDAO
    abstract fun tagDAO(): TagDAO

    companion object {
        private const val DB_NAME = "todo_db"

        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getInstance(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TodoDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}

