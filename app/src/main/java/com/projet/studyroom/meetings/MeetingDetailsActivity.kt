package com.projet.studyroom.meetings

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.projet.studyroom.R

class MeetingDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_details)

        // Récupération des vues
        val tvMeetingTitle = findViewById<TextView>(R.id.tvMeetingTitle)
        val tvMeetingDate = findViewById<TextView>(R.id.tvMeetingDate)
        val tvMeetingDescription = findViewById<TextView>(R.id.tvMeetingDescription)
        val participantsContainer = findViewById<LinearLayout>(R.id.participantsContainer)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Exemple de données (à remplacer par les données réelles)
        val meetingTitle = "Réunion Projet X"
        val meetingDate = "18 Mars 2026 - 14:00"
        val meetingDescription = "Discussion sur l'avancement du projet et prochaines étapes."
        val participants = listOf("Alice", "Bob", "Charlie", "David")

        // Affichage des détails
        tvMeetingTitle.text = meetingTitle
        tvMeetingDate.text = meetingDate
        tvMeetingDescription.text = meetingDescription

        // Affichage des participants dynamiquement
        participantsContainer.removeAllViews() // Sécurité
        participants.forEach { name ->
            val tv = TextView(this).apply {
                text = name
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@MeetingDetailsActivity, R.color.gray_500))
                setPadding(0, 4, 0, 4)
            }
            participantsContainer.addView(tv)
        }

        // Bouton retour
        btnBack.setOnClickListener {
            finish()
        }
    }
}
