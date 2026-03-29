package com.projet.studyroom.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projet.studyroom.databinding.ActivityRegisterBinding
import com.projet.studyroom.home.HomeActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            // Clear errors
            binding.tilName.error = null
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            binding.tilConfirmPassword.error = null

            // Validation
            val name = binding.etName.text.toString().trim()
            if (name.isEmpty()) {
                binding.tilName.error = "Name is required"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.tilEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "Password is required"
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.tilPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                binding.tilConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            // Show loading - disable button
            binding.btnRegister.isEnabled = false

            // Firebase Registration
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.btnRegister.isEnabled = true
                    if (task.isSuccessful) {
                        // Get user UID
                        val uid = auth.currentUser?.uid
                        
                        // Get user name from input
                        val name = binding.etName.text.toString().trim()
                        
                        // Create user data map
                        val userData = hashMapOf(
                            "email" to email,
                            "full_name" to name,
                            "created_at" to System.currentTimeMillis()
                        )
                        
                        // Save user data to Firestore
                        if (uid != null) {
                            db.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Utilisateur créé avec succès!", Toast.LENGTH_SHORT).show()
                                    
                                    // Navigate to Home after successful save
                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Erreur lors de la sauvegarde: ${e.message}", Toast.LENGTH_SHORT).show()
                                    // Still allow navigation even if save fails
                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                }
                        } else {
                            // UID is null, navigate to home anyway
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                        
                        // Registration successful
                        Toast.makeText(this, "Inscription réussie!", Toast.LENGTH_SHORT).show()
                        // Optionally send email verification
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    Toast.makeText(this, "Email de vérification envoyé!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // Registration failed - show structured error
                        val errorMessage = getAuthErrorMessage(task.exception?.message ?: "")
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.tvLogin.setOnClickListener {
            finish()
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
            else -> "Erreur d'inscription: ${error}"
        }
    }
}
