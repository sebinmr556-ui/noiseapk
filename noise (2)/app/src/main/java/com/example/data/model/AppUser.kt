package com.example.data.model

data class AppUser(
    val uid: String = "usr_current",
    val username: String = "sebin_mr",
    val displayName: String = "Sebin",
    val email: String = "sebinmr556@gmail.com",
    val photoURL: String = "",
    val bio: String = "Exploring beats & soundscapes 🎧",
    val website: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isVerified: Boolean = false,
    val isPrivate: Boolean = false,
    val blockedUsers: List<String> = emptyList()
)

data class PublicUserProfile(
    val uid: String,
    val username: String,
    val displayName: String,
    val avatar: String = "",
    val bio: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isVerified: Boolean = false,
    val postsCount: Int = 0
)
