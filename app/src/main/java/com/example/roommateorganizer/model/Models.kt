package com.example.roommateorganizer.model

import com.google.firebase.Timestamp

// Keep ALL firestore data models here (single source of truth).

data class UserDoc(
    val id: String = "",
    val displayName: String = "",
    val handle: String = "",          // e.g., "john#A3B9Q"
    val handleLower: String = "",     // "john#a3b9q"
    val householdId: String? = null,
    val lastNameChangeAt: Timestamp? = null
)

data class ChoreDoc(
    val id: String = "",
    val title: String = "",
    val frequency: String = "",
    val location: String = "",
    val dueAt: Timestamp? = null,
    val assigneeUid: String? = null,
    val completed: Boolean = false
)

data class ExpenseDoc(
    val id: String = "",
    val title: String = "",
    val frequency: String = "",
    val amount: Double = 0.0,
    val splitEvenly: Boolean = true,
    val participants: List<String> = emptyList(), // UIDs
    val paid: Boolean = false,
    val createdAt: Timestamp? = null
)

data class MessageDoc(
    val id: String = "",
    val uid: String = "",
    val text: String = "",
    val createdAt: Timestamp? = null
)
