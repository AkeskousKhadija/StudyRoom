package com.projet.studyroom.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.projet.studyroom.databinding.ActivityLoginBinding
import com.projet.studyroom.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.tilEmail.error = "L'email est requis"
                return@setOnClickListener
            }
            binding.tilEmail.error = null
            
            if (password.isEmpty()) {
                binding.tilPassword.error = "Le mot de passe est requis"
                return@setOnClickListener
            }
            binding.tilPassword.error = null

            // Disable button during login
            binding.btnLogin.isEnabled = false

            // Firebase Login
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.btnLogin.isEnabled = true
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Connexion réussie!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        // Show structured error message
                        val errorMessage = getAuthErrorMessage(task.exception?.message ?: "")
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun getAuthErrorMessage(error: String): String {
        return when {
            error.contains("INVALID_LOGIN_CREDENTIALS") || error.contains("wrong-password") || error.contains("password is invalid") ->
                "Mot de passe incorrect"
            error.contains("no user record") || error.contains("user-not-found") ->
                "Aucun utilisateur trouvé avec cet email"
            error.contains("email-already-in-use") ->
                "Cet email est déjà utilisé"
            error.contains("invalid-email") ->
                "Format d'email invalide"
            error.contains("weak-password") || error.contains("password must be at least") ->
                "Le mot de passe doit contenir au moins 6 caractères"
            error.contains("network") ->
                "Erreur de connexion. Vérifiez votre internet"
            error.contains("too-many-requests") ->
                "Trop de tentatives. Veuillez réessayer plus tard"
            else -> "Erreur de connexion: ${error}"
        }
    }
}
