package com.example.roommateorganizer

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

/**
 * Resolve the current user's householdId, then give you a subcollection ref.
 * Usage:
 *   getHouseholdSub("messages") { col -> ... }
 */
fun getHouseholdSub(
    sub: String,
    onOk: (CollectionReference) -> Unit,
    onFail: (String) -> Unit = {}
) {
    val uid = F.meUid ?: return onFail("Not signed in")
    F.db.collection("users").document(uid).get()
        .addOnSuccessListener { snap ->
            val hh = snap.getString("householdId")
            if (hh.isNullOrBlank()) {
                onFail("No household yet")
            } else {
                onOk(F.db.collection("households").document(hh).collection(sub))
            }
        }
        .addOnFailureListener { onFail(it.message ?: "Error") }
}

/** Access the household doc itself if needed */
fun getHouseholdDoc(
    onOk: (DocumentReference) -> Unit,
    onFail: (String) -> Unit = {}
) {
    val uid = F.meUid ?: return onFail("Not signed in")
    F.db.collection("users").document(uid).get()
        .addOnSuccessListener { snap ->
            val hh = snap.getString("householdId")
            if (hh.isNullOrBlank()) onFail("No household")
            else onOk(F.db.collection("households").document(hh))
        }
        .addOnFailureListener { onFail(it.message ?: "Error") }
}
