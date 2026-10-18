package com.example.data.model

data class CommentItem(
    val id: String,
    val uid: String,
    val username: String,
    val avatar: String,
    val text: String,
    val createdAt: String
)

data class Post(
    val id: String,
    val uid: String,
    val username: String,
    val avatar: String,
    val image: String,
    val caption: String = "",
    val location: String = "",
    val filter: String = "Normal",
    val likes: Int = 0,
    val likedBy: List<String> = emptyList(),
    val savedBy: List<String> = emptyList(),
    val comments: List<CommentItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val mediaType: String = "image"
)
