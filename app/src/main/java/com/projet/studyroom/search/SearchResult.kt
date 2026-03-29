package com.projet.studyroom.search

// Data class for search results
data class SearchResult(
    val id: Int,
    val title: String,
    val description: String,
    val type: String // "group", "meeting", "member"
)
