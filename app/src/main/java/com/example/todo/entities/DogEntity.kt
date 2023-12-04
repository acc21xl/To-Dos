package com.example.todo.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = DogEntity.TABLE_NAME)
data class DogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val imageBytes: ByteArray?,
    val breed: String,
    val birthdayDate: Date,
    val notes: String,
    val deleted: Boolean
){
    companion object {
        const val TABLE_NAME = "dogs"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DogEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (imageBytes != null) {
            if (other.imageBytes == null) return false
            if (!imageBytes.contentEquals(other.imageBytes)) return false
        } else if (other.imageBytes != null) return false
        if (breed != other.breed) return false
        if (birthdayDate != other.birthdayDate) return false
        if (notes != other.notes) return false
        if (deleted != other.deleted) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + (imageBytes?.contentHashCode() ?: 0)
        result = 31 * result + breed.hashCode()
        result = 31 * result + birthdayDate.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + deleted.hashCode()
        return result
    }
}
