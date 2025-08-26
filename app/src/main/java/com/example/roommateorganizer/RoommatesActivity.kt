package com.example.roommateorganizer

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp

class RoommatesActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var searchBtn: Button
    private lateinit var resultText: TextView
    private lateinit var createBtn: Button

    private var foundUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // quick inline UI to keep this file self-contained
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }
        searchInput = EditText(this).apply { hint = "Enter handle e.g. john#A3B9Q" }
        searchBtn = Button(this).apply { text = "Search" }
        resultText = TextView(this)
        createBtn = Button(this).apply { text = "Create / Join household with this user" }

        root.addView(searchInput)
        root.addView(searchBtn)
        root.addView(resultText)
        root.addView(createBtn)
        setContentView(root)

        searchBtn.setOnClickListener { doSearch() }
        createBtn.setOnClickListener { createOrJoin() }
    }

    private fun doSearch() {
        val input = searchInput.text.toString().trim()
        if (!input.contains("#")) {
            Toast.makeText(this, "Enter full handle: name#TAG", Toast.LENGTH_SHORT).show()
            return
        }
        F.db.collection("users")
            .whereEqualTo("handleLower", input.lowercase())
            .limit(1)
            .get()
            .addOnSuccessListener { qs ->
                if (qs.isEmpty) {
                    resultText.text = "No user found"
                    foundUid = null
                } else {
                    val doc = qs.documents.first()
                    foundUid = doc.id
                    resultText.text = "Found: ${doc.getString("displayName")} (${doc.getString("handle")})"
                }
            }
    }

    private fun createOrJoin() {
        val otherUid = foundUid ?: return
        val me = F.meUid ?: return

        // get my or other user's household. If neither has one, create.
        F.db.collection("users").document(me).get().addOnSuccessListener { meSnap ->
            val myHh = meSnap.getString("householdId")
            if (!myHh.isNullOrBlank()) {
                // add other to my household
                val ref = F.db.collection("households").document(myHh)
                ref.update("members", com.google.firebase.firestore.FieldValue.arrayUnion(otherUid))
                F.db.collection("users").document(otherUid).update("householdId", myHh)
                Toast.makeText(this, "Added to your household", Toast.LENGTH_SHORT).show()
            } else {
                // create new household for both
                val hh = hashMapOf(
                    "members" to listOf(me, otherUid),
                    "createdAt" to Timestamp.now()
                )
                val ref = F.db.collection("households").document()
                ref.set(hh).addOnSuccessListener {
                    F.db.collection("users").document(me).update("householdId", ref.id)
                    F.db.collection("users").document(otherUid).update("householdId", ref.id)
                    Toast.makeText(this, "Household created", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
