package com.example.todo

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todo.data.TodoDAO

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 从 Intent 中获取标题和消息
        val title = intent.getStringExtra("title") ?: "Default Title"
        val message = intent.getStringExtra("message") ?: "Default Message"


        MyNotification(context, title, message).FirNotification()
    }
}