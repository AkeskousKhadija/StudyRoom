package com.projet.studyroom.meetings

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projet.studyroom.R
import java.util.*

class MeetingActivity : AppCompatActivity() {

    private lateinit var meetingTimer: TextView
    private lateinit var micIcon: ImageView
    private lateinit var cameraIcon: ImageView
    private lateinit var featuredParticipant: CardView
    private lateinit var featuredSpeakingIndicator: View
    private lateinit var chatOverlay: LinearLayout
    private lateinit var participantsButton: LinearLayout
    private lateinit var endCallButton: LinearLayout
    private lateinit var copyCodeButton: ImageView

    private var isMicMuted = false
    private var isCameraOff = false
    private var isChatVisible = false
    private var seconds = 0
    private var timerRunning = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)

        // Gestion des insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupListeners()
        startTimer()

        // Simulation d'activité de parole
        simulateSpeakingActivity()
    }

    private fun initViews() {
        meetingTimer = findViewById(R.id.meetingTimer)
        micIcon = findViewById(R.id.micIcon)
        cameraIcon = findViewById(R.id.cameraIcon)
        featuredParticipant = findViewById(R.id.featuredParticipant)
        featuredSpeakingIndicator = findViewById(R.id.featuredSpeakingIndicator)
        chatOverlay = findViewById(R.id.chatOverlay)  // Attention: majuscule 'O'
        participantsButton = findViewById(R.id.participantsButton)
        endCallButton = findViewById(R.id.endCallButton)
        copyCodeButton = findViewById(R.id.copyCodeButton)

        // Initialisation des avatars cliquables

    }

    private fun setupListeners() {

        // Partager écran
        findViewById<LinearLayout>(R.id.shareScreenButton).setOnClickListener {
            Toast.makeText(this, "Partage d'écran démarré", Toast.LENGTH_SHORT).show()
        }

        // Participants (ouvrir la liste)
        participantsButton.setOnClickListener {
            Toast.makeText(this, "Liste des participants", Toast.LENGTH_SHORT).show()
            showParticipantsList()
        }

        // Copier le code de réunion
        copyCodeButton.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Meeting Code", "abc-defg-hij")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Code copié", Toast.LENGTH_SHORT).show()
        }

        // Raccrocher
        endCallButton.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Quitter la réunion")
                .setMessage("Voulez-vous vraiment quitter cette réunion ?")
                .setPositiveButton("Oui") { _, _ ->
                    timerRunning = false
                    finish()
                }
                .setNegativeButton("Non", null)
                .show()
        }

        // Chat overlay (accessible via un bouton)
        findViewById<ImageView>(R.id.sendChatButton)?.setOnClickListener {
            Toast.makeText(this, "Message envoyé", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer() {
        Thread {
            while (timerRunning) {
                try {
                    Thread.sleep(1000)
                    seconds++
                    runOnUiThread {
                        updateTimerDisplay()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun updateTimerDisplay() {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        val timeFormatted = if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)
        }

        meetingTimer.text = timeFormatted
    }

    private fun simulateSpeakingActivity() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Simulation aléatoire de quelqu'un qui parle
                featuredSpeakingIndicator.visibility = View.VISIBLE
                featuredSpeakingIndicator.postDelayed({
                    featuredSpeakingIndicator.visibility = View.GONE
                }, 2000)

                handler.postDelayed(this, (5000 + Random().nextInt(10000)).toLong())
            }
        }, 3000)
    }

    private fun showParticipantOptions(participantName: String) {
        val options = arrayOf("Épingler", "Couper le son", "Envoyer un message", "Voir le profil")

        android.app.AlertDialog.Builder(this)
            .setTitle(participantName)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> Toast.makeText(this, "$participantName épinglé", Toast.LENGTH_SHORT).show()
                    1 -> Toast.makeText(this, "Son coupé pour $participantName", Toast.LENGTH_SHORT).show()
                    2 -> Toast.makeText(this, "Message à $participantName", Toast.LENGTH_SHORT).show()
                    3 -> Toast.makeText(this, "Profil de $participantName", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun showParticipantsList() {
        val participants = arrayOf(
            "Vous (hôte)",
            "Jean Durand",
            "Marie Durand",
            "Dr. Martin",
            "Sophie Dubois",
            "Pierre Lambert (audio)",
            "Thomas Petit (audio)"
        )

        android.app.AlertDialog.Builder(this)
            .setTitle("Participants (7)")
            .setItems(participants) { _, which ->
                if (which > 0) {
                    showParticipantOptions(participants[which])
                }
            }
            .setPositiveButton("Inviter", null)
            .show()
    }


}
