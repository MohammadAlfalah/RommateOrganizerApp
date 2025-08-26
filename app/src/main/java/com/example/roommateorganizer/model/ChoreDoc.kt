package com.example.roommateorganizer.model

import com.google.firebase.Timestamp

data class ChoreDoc(
    val id: String? = null,
    val title: String = "",
    val frequency: String? = null,
    val location: String? = null,
    val dueAt: Timestamp? = null,
    val assigneeUid: String? = null,
    val completed: Boolean = false
)
