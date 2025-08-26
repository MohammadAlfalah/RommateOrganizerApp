package com.example.roommateorganizer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.roommateorganizer.model.UserDoc

class RegisterActivity : AppCompatActivity() {

    private fun randomTag(n: Int): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..n).map { chars.random() }.joinToString("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_fixed) // make sure this exists

        val name = findViewById<EditText>(R.id.registerName)
        val email = findViewById<EditText>(R.id.registerEmail)
        val pass = findViewById<EditText>(R.id.registerPassword)
        val btn = findViewById<Button>(R.id.registerButton)

        btn.setOnClickListener {
            val n = name.text.toString().trim()
            val e = email.text.toString().trim()
            val p = pass.text.toString().trim()
            if (n.isBlank() || e.isBlank() || p.isBlank()) return@setOnClickListener

            F.auth.createUserWithEmailAndPassword(e, p).addOnSuccessListener {
                val uid = it.user!!.uid
                val handle = "$n#${randomTag(5)}"
                val doc = UserDoc(
                    displayName = n,
                    handle = handle,
                    handleLower = handle.lowercase()
                )
                F.db.collection("users").document(uid).set(doc)
                    .addOnSuccessListener { Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}
