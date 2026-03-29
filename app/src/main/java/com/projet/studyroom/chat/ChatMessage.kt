package com.projet.studyroom.chat

/**
 * Data class representing a chat message in a group conversation
 */
data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: Int, // Resource drawable ID
    val content: String,
    val timestamp: Long,
    val isFromCurrentUser: Boolean = false
)