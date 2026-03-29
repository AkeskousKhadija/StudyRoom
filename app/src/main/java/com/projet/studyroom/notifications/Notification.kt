package com.projet.studyroom.notifications

import com.projet.studyroom.R

// Data class for notifications
data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val icon: Int = R.drawable.ic_notification
)
