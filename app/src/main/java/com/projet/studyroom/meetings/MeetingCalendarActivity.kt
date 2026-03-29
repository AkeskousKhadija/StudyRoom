package com.projet.studyroom.meetings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.projet.studyroom.databinding.ActivityMeetingCalendarBinding

class MeetingCalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeetingCalendarBinding
    private lateinit var meetingAdapter: CalendarMeetingAdapter

    // Variables pour stocker les infos du groupe
    private var groupId: String? = null
    private var groupName: String? = null

    // Sample meetings data - past and upcoming
    private val allMeetings = listOf(
        // Past meetings
        CalendarMeeting(
            id = 1,
            title = "Math Study Group",
            description = "Algebra and calculus practice",
            date = "Monday, Feb 24",
            time = "10:00 AM - 11:30 AM",
            location = "Room 101",
            isPast = true,
            isOngoing = false
        ),
        CalendarMeeting(
            id = 2,
            title = "Physics Workshop",
            description = "Mechanics and thermodynamics",
            date = "Wednesday, Feb 26",
            time = "2:00 PM - 3:30 PM",
            location = "Lab 3",
            isPast = true,
            isOngoing = false
        ),
        CalendarMeeting(
            id = 3,
            title = "Chemistry Lab",
            description = "Organic chemistry experiments",
            date = "Saturday, Mar 1",
            time = "9:00 AM - 11:00 AM",
            location = "Chemistry Lab",
            isPast = true,
            isOngoing = false
        ),
        // Ongoing meeting
        CalendarMeeting(
            id = 4,
            title = "English Conversation",
            description = "English language practice",
            date = "Today",
            time = "3:00 PM - 4:30 PM",
            location = "Room 205",
            isPast = false,
            isOngoing = true
        ),
        // Upcoming meetings
        CalendarMeeting(
            id = 5,
            title = "Biology Study",
            description = "Cell biology review",
            date = "Tomorrow",
            time = "11:00 AM - 12:30 PM",
            location = "Room 102",
            isPast = false,
            isOngoing = false
        ),
        CalendarMeeting(
            id = 6,
            title = "History Club",
            description = "World War II discussion",
            date = "Wednesday, Mar 5",
            time = "4:00 PM - 5:30 PM",
            location = "Room 301",
            isPast = false,
            isOngoing = false
        ),
        CalendarMeeting(
            id = 7,
            title = "Math Advanced",
            description = "Advanced mathematics",
            date = "Friday, Mar 7",
            time = "2:00 PM - 3:30 PM",
            location = "Room 101",
            isPast = false,
            isOngoing = false
        ),
        CalendarMeeting(
            id = 8,
            title = "Physics Review",
            description = "Exam preparation",
            date = "Saturday, Mar 8",
            time = "10:00 AM - 12:00 PM",
            location = "Lab 2",
            isPast = false,
            isOngoing = false
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupérer les infos du groupe passées en paramètre
        groupId = intent.getStringExtra("group_id")
        groupName = intent.getStringExtra("group_name")

        // Mettre à jour le titre si un nom de groupe est fourni
        if (!groupName.isNullOrEmpty()) {
            binding.tvTitle.text = groupName
        }

        setupRecyclerView()

        // Back button
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        meetingAdapter = CalendarMeetingAdapter { meeting ->
            // Handle meeting click - could navigate to detail
        }

        binding.rvMeetings.apply {
            layoutManager = LinearLayoutManager(this@MeetingCalendarActivity)
            adapter = meetingAdapter
        }

        // Show all meetings
        meetingAdapter.submitList(allMeetings)
    }
}
