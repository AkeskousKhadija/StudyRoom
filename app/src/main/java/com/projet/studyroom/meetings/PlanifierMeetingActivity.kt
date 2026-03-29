package com.projet.studyroom.meetings

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.projet.studyroom.R
import java.util.*

class PlanifierMeetingActivity : AppCompatActivity() {

    private lateinit var etDate: EditText
    private lateinit var etHeureDebut: EditText
    private lateinit var etHeureFin: EditText
    private lateinit var btnCreate: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planifier_meeting)

        etDate = findViewById(R.id.etDate)
        etHeureDebut = findViewById(R.id.etHeureDebut)
        etHeureFin = findViewById(R.id.etHeureFin)
        btnCreate = findViewById(R.id.btnCreate)
        btnCancel = findViewById(R.id.btnCancel)

        // 📅 Date
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this,
                { _, y, m, d -> etDate.setText("$d/${m + 1}/$y") },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // ⏰ Heure début
        etHeureDebut.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this,
                { _, h, min -> etHeureDebut.setText(String.format("%02d:%02d", h, min)) },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        // ⏰ Heure fin
        etHeureFin.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this,
                { _, h, min -> etHeureFin.setText(String.format("%02d:%02d", h, min)) },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        // ✅ Créer
        btnCreate.setOnClickListener {
            finish()
        }

        // ❌ Annuler avec confirmation
        btnCancel.setOnClickListener {
            showCancelDialog()
        }
    }

    // 🔥 DIALOG CONFIRMATION
    private fun showCancelDialog() {
        AlertDialog.Builder(this)
            .setTitle("Annulation")
            .setMessage("Voulez-vous vraiment annuler ce meeting ?")
            .setPositiveButton("Oui") { _, _ ->
                // retour calendrier
                val intent = Intent(this, MeetingCalendarActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Non", null)
            .show()
    }
}
