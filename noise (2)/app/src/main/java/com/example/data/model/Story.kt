package com.example.data.model

data class Story(
    val id: String,
    val uid: String,
    val username: String,
    val avatar: String,
    val mediaUrl: String,
    val caption: String = "",
    val timestamp: String = "Just now",
    val hasSeen: Boolean = false
)
