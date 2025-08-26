package com.example.roommateorganizer

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object F {
    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
    val meUid get() = auth.currentUser?.uid
    var householdId: String? = null // set this after login / household join
    fun now() = com.google.firebase.Timestamp.now()
}

