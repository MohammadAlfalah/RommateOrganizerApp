package com.example.roommateorganizer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.roommateorganizer.model.UserDoc

class LoginActivity : AppCompatActivity() {

    private fun ensureUserProfileExists(uid: String, displayNameFallback: String) {
        val users = F.db.collection("users").document(uid)
        users.get().addOnSuccessListener { snap ->
            if (!snap.exists()) {
                val name = if (displayNameFallback.isNotBlank()) displayNameFallback else "User"
                val handle = "${name}#A123B" // basic fallback
                val doc = UserDoc(
                    displayName = name,
                    handle = handle,
                    handleLower = handle.lowercase()
                )
                users.set(doc)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // make sure this exists

        val email = findViewById<EditText>(R.id.emailInput)
        val pass = findViewById<EditText>(R.id.passwordInput)
        val btn = findViewById<Button>(R.id.loginButton)

        btn.setOnClickListener {
            val e = email.text.toString().trim()
            val p = pass.text.toString().trim()
            F.auth.signInWithEmailAndPassword(e, p).addOnSuccessListener {
                val uid = F.auth.currentUser!!.uid
                ensureUserProfileExists(uid, F.auth.currentUser?.displayName ?: "")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
