package com.example.todo.entities

data class TypicalTodo(
    val title: String,
    val description: String
)

val typicalTodos = listOf(
    TypicalTodo("Take dog on walk", "Walk the dog for 30 minutes."),
    TypicalTodo("Give dog breakfast", "Provide breakfast to the dog."),
    TypicalTodo("Give dog lunch", "Provide lunch to the dog."),
    TypicalTodo("Give dog dinner", "Provide dinner to the dog."),
    TypicalTodo("Bathe dog", "Make sure the dog is nice and clean!"),
    TypicalTodo("Go for Vet check-up", "Make sure the dog is happy and healthy!")
)
