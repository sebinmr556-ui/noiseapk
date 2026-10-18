package com.example.data.model

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val avatar: String,
    val type: NotificationType = NotificationType.LIKE,
    val isRead: Boolean = false
)

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    VOICE_ROOM_INVITE,
    COIN_REWARD
}
