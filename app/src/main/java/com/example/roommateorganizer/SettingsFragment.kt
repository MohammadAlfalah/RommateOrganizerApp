package com.example.roommateorganizer

import android.os.Bundle
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.roommateorganizer.model.UserDoc
import com.google.firebase.Timestamp

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_prefs, rootKey)

        val namePref = findPreference<EditTextPreference>("display_name")
        val passwordPref = findPreference<Preference>("change_password")
        val logoutPref = findPreference<Preference>("logout")

        val uid = F.meUid ?: return

        // Load and show current name
        F.db.collection("users").document(uid).get().addOnSuccessListener { snap ->
            val name = snap.getString("displayName") ?: ""
            namePref?.text = name
            namePref?.summary = name
        }

        namePref?.setOnPreferenceChangeListener { _, newValue ->
            val newName = (newValue as? String)?.trim().orEmpty()
            if (newName.isBlank()) {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnPreferenceChangeListener false
            }

            F.db.collection("users").document(uid).get().addOnSuccessListener { snap ->
                val last = snap.getTimestamp("lastNameChangeAt")
                val now = Timestamp.now()
                if (last != null) {
                    val diffDays = (now.seconds - last.seconds) / (60 * 60 * 24)
                    if (diffDays < 30) {
                        Toast.makeText(requireContext(), "You can change name every 30 days", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }
                }
                F.db.collection("users").document(uid)
                    .update(mapOf("displayName" to newName, "lastNameChangeAt" to now))
                Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show()
                namePref.summary = newName
            }
            false // we manually set summary; do not auto-persist by prefs (FireStore is source of truth)
        }

        passwordPref?.setOnPreferenceClickListener {
            // Simplified: trigger Firebase password reset email (if using email/password)
            val email = F.auth.currentUser?.email
            if (email.isNullOrBlank()) {
                Toast.makeText(requireContext(), "No email on account", Toast.LENGTH_SHORT).show()
            } else {
                F.auth.sendPasswordResetEmail(email)
                Toast.makeText(requireContext(), "Reset email sent", Toast.LENGTH_SHORT).show()
            }
            true
        }

        logoutPref?.setOnPreferenceClickListener {
            F.auth.signOut()
            Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show()
            activity?.finish()
            true
        }
    }
}
