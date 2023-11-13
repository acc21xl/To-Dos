package com.example.tailtasks.utils

import androidx.room.TypeConverter
import com.example.tailtasks.enums.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }

}
