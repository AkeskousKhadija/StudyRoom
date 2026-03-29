package com.projet.studyroom.meetings

// Data class for calendar meetings
data class CalendarMeeting(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val isPast: Boolean = false,
    val isOngoing: Boolean = false
)
