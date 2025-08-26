package com.example.roommateorganizer

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Theme before content
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_theme", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Firebase init (safe)
        val app = FirebaseApp.initializeApp(this)
        if (app == null) {
            Log.w("Firebase", "Default FirebaseApp not initialized. Check google-services.json & plugin.")
        } else {
            Log.d("Firebase", "Initialized")
        }

        // Views (nullable-safe)
        val catImage: ImageView? = findViewById(R.id.catImage)
        val appName: TextView? = findViewById(R.id.MyFApp)
        val logoBlock: LinearLayout? = findViewById(R.id.logoBlock)
        logoBlock?.alpha = 0f

        // ✅ Deep link scheme fixed to match manifest: roomey://add
        intent?.data?.let { data ->
            if (data.scheme == "roomey" && data.host == "add") {
                val title = data.getQueryParameter("title")
                val datetime = data.getQueryParameter("datetime")
                if (!title.isNullOrBlank() && !datetime.isNullOrBlank()) {
                    prefs.edit()
                        .putString("shared_title", title)
                        .putString("shared_datetime", datetime)
                        .apply()
                }
            }
        }

        val startNext: () -> Unit = {
            val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
            val next = if (isLoggedIn) MainActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this, next))
            finish()
        }

        // If any required view is missing, skip animation and continue
        if (appName == null || logoBlock == null || catImage == null) {
            Log.w("Splash", "Missing views in activity_splash. Skipping animation.")
            Handler(mainLooper).postDelayed({ startNext() }, 600) // quick handoff
            return
        }

        appName.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                appName.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val fadeIn = ObjectAnimator.ofFloat(logoBlock, View.ALPHA, 0f, 1f).apply {
                    duration = 1000
                }

                val originalY = catImage.y
                val hangY = originalY + 20f
                val climbY = originalY - 40f

                val hangDown = ObjectAnimator.ofFloat(catImage, View.Y, originalY, hangY).setDuration(300)
                val climbUp = ObjectAnimator.ofFloat(catImage, View.Y, hangY, climbY).setDuration(300)
                val settle = ObjectAnimator.ofFloat(catImage, View.Y, climbY, originalY).setDuration(300)

                val shake = ObjectAnimator.ofFloat(catImage, View.ROTATION, -10f, 10f).apply {
                    duration = 100
                    repeatCount = 5
                    repeatMode = ValueAnimator.REVERSE
                }

                val moveSequence = AnimatorSet().apply { playSequentially(hangDown, climbUp, settle, shake) }

                AnimatorSet().apply {
                    playTogether(fadeIn, moveSequence)
                    start()
                }

                Handler(mainLooper).postDelayed({ startNext() }, 3000)
            }
        })
    }
}
