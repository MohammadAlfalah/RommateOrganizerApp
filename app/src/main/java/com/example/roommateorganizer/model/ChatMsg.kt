package com.example.roommateorganizer.model

import com.google.firebase.Timestamp

data class ChatMsg(
    val id: String = "",
    val uid: String = "",
    val name: String = "",       // what we show in the list
    val text: String = "",
    val createdAt: Timestamp? = null,
    val mine: Boolean = false    // convenient flag for “sent by me”
)
