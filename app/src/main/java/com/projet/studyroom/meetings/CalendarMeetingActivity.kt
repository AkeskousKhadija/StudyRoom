package com.projet.studyroom.meetings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.projet.studyroom.R
import com.projet.studyroom.databinding.ActivityCalendarMeetingBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CalendarMeetingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarMeetingBinding
    private val meetingsList = mutableListOf<CalendarMeeting>()
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.FRENCH)
    private var groupId: String? = null
    private var groupName: String? = null
    private var selectedDate: Date = Date()
    private var currentMonth: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupérer les infos du groupe
        groupId = intent.getStringExtra("group_id")
        groupName = intent.getStringExtra("group_name")

        setupCalendar()
        setupFab()
        loadMeetings()
    }

    private fun setupCalendar() {
        // Afficher le mois actuel
        binding.txtMonthDisplay.text = dateFormat.format(currentMonth.time)
        
        // Configurer le calendrier pour afficher en français
        binding.calendarView.setLocale(TimeZone.getTimeZone("Europe/Paris"), Locale.FRENCH)
        binding.calendarView.setUseThreeLetterAbbreviation(true)
        
        // Navigation entre les mois avec les boutons
        binding.btnPreviousMonth.setOnClickListener {
            currentMonth.add(Calendar.MONTH, -1)
            binding.txtMonthDisplay.text = dateFormat.format(currentMonth.time)
            binding.calendarView.setCurrentDate(currentMonth.time)
        }

        binding.btnNextMonth.setOnClickListener {
            currentMonth.add(Calendar.MONTH, 1)
            binding.txtMonthDisplay.text = dateFormat.format(currentMonth.time)
            binding.calendarView.setCurrentDate(currentMonth.time)
        }

        // CompactCalendarView listener - gérer le clic sur un jour
        binding.calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                dateClicked?.let {
                    selectedDate = it
                    showMeetingsForDate()
                    Toast.makeText(this@CalendarMeetingActivity, 
                        "Date: ${SimpleDateFormat("dd MMMM", Locale.FRENCH).format(it)}", 
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                firstDayOfNewMonth?.let {
                    currentMonth.time = it
                    binding.txtMonthDisplay.text = dateFormat.format(it)
                }
            }
        })
    }

    private fun setupFab() {
        binding.btnPlanifier.setOnClickListener {
            // Ouvrir l'écran pour planifier un nouveau meeting
            val intent = android.content.Intent(this, PlanifierMeetingActivity::class.java)
            intent.putExtra("group_id", groupId)
            intent.putExtra("group_name", groupName)
            startActivity(intent)
        }
    }

    private fun loadMeetings() {
        // Charger les meetings (ici en dur, à remplacer par des données réelles depuis Firestore)
        meetingsList.clear()
        meetingsList.addAll(getSampleMeetings())
        
        // Afficher les meetings du jour
        showMeetingsForDate()
    }

    private fun getSampleMeetings(): List<CalendarMeeting> {
        return listOf(
            // Meetings à venir
            CalendarMeeting(
                id = 1,
                title = "Math Study Group",
                description = "Algebra and calculus practice",
                date = "Lundi",
                time = "10:00 - 11:30",
                location = "Salle 101",
                isPast = false,
                isOngoing = false
            ),
            CalendarMeeting(
                id = 2,
                title = "Physics Workshop",
                description = "Mechanics and thermodynamics",
                date = "Mercredi",
                time = "14:00 - 15:30",
                location = "Labo 3",
                isPast = false,
                isOngoing = false
            ),
            CalendarMeeting(
                id = 3,
                title = "Chemistry Lab",
                description = "Organic chemistry experiments",
                date = "Samedi",
                time = "09:00 - 11:00",
                location = "Chimie Lab",
                isPast = false,
                isOngoing = false
            ),
            // Meeting en cours
            CalendarMeeting(
                id = 4,
                title = "English Conversation",
                description = "English language practice",
                date = "Aujourd'hui",
                time = "15:00 - 16:30",
                location = "Salle 205",
                isPast = false,
                isOngoing = true
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun showMeetingsForDate() {
        binding.meetingsListContainer.removeAllViews()
        
        // Filtrer les meetings pour la date sélectionnée
        val filteredMeetings = meetingsList.filter { !it.isPast }
        
        if (filteredMeetings.isEmpty()) {
            binding.emptyStateContainer.visibility = View.VISIBLE
            binding.meetingsListContainer.visibility = View.GONE
        } else {
            binding.emptyStateContainer.visibility = View.GONE
            binding.meetingsListContainer.visibility = View.VISIBLE
            
            for (meeting in filteredMeetings) {
                val meetingView = layoutInflater.inflate(
                    R.layout.item_calendar_meeting,
                    binding.meetingsListContainer,
                    false
                )
                
                meetingView.findViewById<TextView>(R.id.tvMeetingTitle).text = meeting.title
                meetingView.findViewById<TextView>(R.id.tvMeetingTime).text = meeting.time
                meetingView.findViewById<TextView>(R.id.tvMeetingLocation).text = meeting.location
                
                binding.meetingsListContainer.addView(meetingView)
            }
        }
        
        // Mettre à jour le compteur
        binding.txtMeetingCount.text = "${filteredMeetings.size} meeting"
        
        // Mettre à jour les badges
        val upcomingCount = meetingsList.filter { !it.isPast }.size
        val pastCount = meetingsList.filter { it.isPast }.size
        
        if (upcomingCount > 0) {
            binding.badgeUpcoming.text = upcomingCount.toString()
            binding.badgeUpcoming.visibility = View.VISIBLE
        } else {
            binding.badgeUpcoming.visibility = View.GONE
        }
        
        if (pastCount > 0) {
            binding.badgePast.text = pastCount.toString()
            binding.badgePast.visibility = View.VISIBLE
        } else {
            binding.badgePast.visibility = View.GONE
        }
    }
}