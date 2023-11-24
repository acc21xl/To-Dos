package com.example.todo

import android.app.Application
import com.example.todo.data.TodoDatabase

class TodoApplication : Application() {
    val todoDatabase by lazy { TodoDatabase.getInstance(this) }
    val todoDAO by lazy { todoDatabase.todoDAO() }
}

