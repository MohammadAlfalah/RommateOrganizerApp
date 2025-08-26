package com.example.roommateorganizer

data class Task(
    val id: String = "",
    val title: String = "",
    val dueAt: Long = 0L,           // epoch millis
    val assignedTo: String = "",    // uid or name
    val isCompleted: Boolean = false
)