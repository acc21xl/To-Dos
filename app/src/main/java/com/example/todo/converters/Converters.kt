package com.example.todo.converters

import androidx.room.TypeConverter
import java.util.*

/**
 * A utility class to convert between Date objects and timestamps for Room database.
 * Room does not know how to store custom types like Date.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
