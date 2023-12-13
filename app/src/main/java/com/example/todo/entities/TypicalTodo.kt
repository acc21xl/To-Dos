package com.example.todo.entities
/**
 * Data class representing a typical todo item.
 * This class is used to define standard or common tasks (todos) that can be reused
 *
 * This class is not a Room Entity but a simple data class to represent reusable todo templates
 */
data class TypicalTodo(
    val title: String,
    val description: String
)

/**
 * This list includes common tasks related to dog care, such as walking, feeding, and vet visits
 * These items serve as templates or suggestions that can be used to quickly create new todos
 */
val typicalTodos = listOf(
    TypicalTodo("Take dog on walk", "Walk the dog for 30 minutes."),
    TypicalTodo("Give dog breakfast", "Provide breakfast to the dog."),
    TypicalTodo("Give dog lunch", "Provide lunch to the dog."),
    TypicalTodo("Give dog dinner", "Provide dinner to the dog."),
    TypicalTodo("Bathe dog", "Make sure the dog is nice and clean!"),
    TypicalTodo("Go for Vet check-up", "Make sure the dog is happy and healthy!")
)
