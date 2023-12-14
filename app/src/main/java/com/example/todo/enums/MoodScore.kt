package com.example.todo.enums


/**
 * Enum representing various mood scores.
 * Each enum value corresponds to a specific mood score, represented by an integer
 * This enum is used to quantify and categorize the mood of the dog
 */
enum class MoodScore(val score: Int) {
    UPSET(1), SAD(2), NEUTRAL(3), HAPPY(4), EXCELLENT(5)
}
