package com.example.todo.enums

/**
 * Enum representing various permission statuses in the application
 * Used to indicate the state of necessary permissions such as alarm and notification permissions
 */
enum class PermissionStatus {
    BOTH_GRANTED, ALARM_DENIED, NOTIFICATION_DENIED, BOTH_DENIED
}
