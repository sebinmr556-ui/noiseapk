package com.example.data.model

data class RoomParticipant(
    val uid: String,
    val username: String,
    val displayName: String,
    val avatar: String,
    val isHost: Boolean = false,
    val isSpeaker: Boolean = false,
    val isMuted: Boolean = false,
    val isSpeaking: Boolean = false,
    val joinedAt: Long = System.currentTimeMillis(),
    val seatNumber: Int = 0
)

data class VoiceRoom(
    val id: String,
    val title: String,
    val topic: String,
    val hostUid: String,
    val hostUsername: String,
    val hostDisplayName: String,
    val hostAvatar: String,
    val speakers: List<RoomParticipant> = emptyList(),
    val listeners: List<RoomParticipant> = emptyList(),
    val tags: List<String> = emptyList(),
    val ambientVibe: String = "lofi", // lofi, cafe, cyber, acoustic, none
    val isLive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val slotLikes: Map<Int, Int> = emptyMap()
)
