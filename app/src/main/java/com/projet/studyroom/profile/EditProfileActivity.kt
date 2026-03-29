package com.projet.studyroom.profile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projet.studyroom.R

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var editName: EditText
    private lateinit var editBio: EditText
    private lateinit var editInterests: EditText
    private lateinit var btnSaveProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        editName = findViewById(R.id.editName)
        editBio = findViewById(R.id.editBio)
        editInterests = findViewById(R.id.editInterests)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        // Back button
        val backButton = findViewById<ImageView>(R.id.ivBack)
        if (backButton != null) {
            backButton.setOnClickListener {
                finish()
            }
        }

        // Load current user data
        loadUserData()

        // Save button
        btnSaveProfile.setOnClickListener {
            saveUserData()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Aucun utilisateur connecté", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = currentUser.uid

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fullName = document.getString("full_name")
                    val bio = document.getString("bio")
                    val interests = document.getString("interests")

                    if (fullName != null) {
                        editName.setText(fullName)
                    }

                    if (bio != null) {
                        editBio.setText(bio)
                    }

                    if (interests != null) {
                        editInterests.setText(interests)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Aucun utilisateur connecté", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val newName = editName.text.toString().trim()
        val newBio = editBio.text.toString().trim()
        val newInterests = editInterests.text.toString().trim()

        if (newName.isEmpty()) {
            editName.error = "Le nom est requis"
            return
        }

        // Create update map
        val userData = hashMapOf(
            "full_name" to newName,
            "bio" to newBio,
            "interests" to newInterests,
            "updated_at" to System.currentTimeMillis()
        )

        db.collection("users").document(userId)
            .update(userData as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Profil mis à jour avec succès!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
