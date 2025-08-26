// com/example/roommateorganizer/model/MessageMapping.kt
package com.example.roommateorganizer.model

import com.google.firebase.Timestamp

/**
 * Map a Firestore message document to the UI model ChatMsg.
 * Only uses safe, guaranteed fields so it compiles even if
 * your document has no 'name'/'senderName'/'displayName'.
 */
fun MessageDoc.toChatMsg(meUid: String?): ChatMsg =
    ChatMsg(
        id        = id ?: "",
        uid       = uid.orEmpty(),
        // show uid as the name for now; you can replace this with a real
        // display name later when you actually store it on the document
        name      = uid.orEmpty(),
        text      = text.orEmpty(),
        createdAt = createdAt ?: Timestamp.now(),
        mine      = (meUid != null && uid == meUid)
    )
