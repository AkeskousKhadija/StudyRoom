package com.projet.studyroom.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.projet.studyroom.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        val currentUser = auth.currentUser
        
        // Navigate after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            if (currentUser != null) {
                // User is logged in, go to Home
                startActivity(Intent(this, com.projet.studyroom.home.HomeActivity::class.java))
            } else {
                // User is not logged in, go to Onboarding
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
