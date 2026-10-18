package com.example.data.model

data class DirectMessage(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val mediaUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true
)

data class Conversation(
    val targetUser: PublicUserProfile,
    val lastMessage: DirectMessage,
    val unreadCount: Int = 0
)
