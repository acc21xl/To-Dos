package com.example.todo.enums

enum class RepeatFrequency(val value: Int) {
        Daily(0),
        Weekly(1),
        Fortnightly(2),
        Monthly(3),
        Yearly(4);

        companion object {
                fun fromInt(value: Int) = values().firstOrNull { it.value == value }
        }
}
