package com.projet.studyroom.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.projet.studyroom.databinding.ActivityOnboarding1Binding
import com.projet.studyroom.databinding.ActivityOnboarding2Binding

class OnboardingActivity : AppCompatActivity() {

    private var currentScreen = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start with first onboarding screen
        showOnboarding1()
    }

    private fun showOnboarding1() {
        val binding = ActivityOnboarding1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            showOnboarding2()
        }
    }

    private fun showOnboarding2() {
        val binding = ActivityOnboarding2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            // Navigate to Login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
