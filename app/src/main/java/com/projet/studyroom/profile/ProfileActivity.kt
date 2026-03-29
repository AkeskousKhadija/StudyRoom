package com.projet.studyroom.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projet.studyroom.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Views
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileBio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        profileName = findViewById(R.id.profileName)
        profileEmail = findViewById(R.id.profileEmail)
        profileBio = findViewById(R.id.profileBio)

        // Load user data
        loadUserData()

        // Back button
        val backButton = findViewById<ImageView>(R.id.ivBack)
        if (backButton != null) {
            backButton.setOnClickListener {
                finish()
            }
        }

        // Button Edit Profile
        val editButton = findViewById<Button>(R.id.btnEditProfile)

        editButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val avatarButton = findViewById<Button>(R.id.btnCreateAvatar)

        avatarButton.setOnClickListener {
            val intent = Intent(this, AvatarActivity::class.java)
            startActivity(intent)
        }

        val languageMenu = findViewById<LinearLayout>(R.id.menuLanguage)

        languageMenu.setOnClickListener {
            val intent = Intent(this, com.projet.studyroom.auth.Language_activity::class.java)
            startActivity(intent)
        }

        val appearancebtn = findViewById<LinearLayout>(R.id.menuappearance)

        appearancebtn.setOnClickListener {
            val intent = Intent(this, com.projet.studyroom.home.Appearance::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload user data when returning from EditProfileActivity
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Aucun utilisateur connecté", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fullName = document.getString("full_name")
                    val email = document.getString("email")
                    val bio = document.getString("bio")

                    if (fullName != null) {
                        profileName.text = fullName
                    }

                    if (email != null) {
                        profileEmail.text = email
                    }

                    if (bio != null && bio.isNotEmpty()) {
                        profileBio.text = bio
                    } else {
                        profileBio.text = "Étudiant en ingénierie logicielle"
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
