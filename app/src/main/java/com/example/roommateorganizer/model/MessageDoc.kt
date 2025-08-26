package com.example.roommateorganizer.model

import com.google.firebase.Timestamp

/** Firestore message document model (field names are intentionally nullable). */
data class MessageDoc(
    val id: String? = null,
    val uid: String? = null,          // author uid
    val senderName: String? = null,   // optional
    val displayName: String? = null,  // optional
    val name: String? = null,         // optional (some schemas use "name")
    val text: String? = null,
    val createdAt: Timestamp? = null
)
