package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NoiseRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("noise_app_prefs", Context.MODE_PRIVATE)

    // Current User (clean real profile, no mock dummy data)
    private val _currentUser = MutableStateFlow(
        AppUser(
            uid = "usr_current",
            username = prefs.getString("username", "sebin_mr") ?: "sebin_mr",
            displayName = prefs.getString("display_name", "Sebin") ?: "Sebin",
            email = "sebinmr556@gmail.com",
            photoURL = prefs.getString("photo_url", "") ?: "",
            bio = prefs.getString("bio", "") ?: "",
            website = prefs.getString("website", "") ?: "",
            followersCount = prefs.getInt("followers_count", 0),
            followingCount = prefs.getInt("following_count", 0),
            isVerified = false,
            isPrivate = prefs.getBoolean("is_private", false)
        )
    )
    val currentUser: StateFlow<AppUser> = _currentUser.asStateFlow()

    // Coins Balance
    private val _coinsBalance = MutableStateFlow(prefs.getInt("coins_balance", 100))
    val coinsBalance: StateFlow<Int> = _coinsBalance.asStateFlow()

    // Following List (clean set)
    private val _followingList = MutableStateFlow(
        prefs.getStringSet("following_list", emptySet())?.toSet() ?: emptySet()
    )
    val followingList: StateFlow<Set<String>> = _followingList.asStateFlow()

    // Discoverable Creators (empty - no dummy accounts)
    val discoverableUsers: List<PublicUserProfile> = emptyList()

    // Posts (starts clean, no dummy posts)
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // Stories (starts clean, no dummy stories)
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    // Live Voice Rooms (starts clean, no dummy rooms)
    private val _voiceRooms = MutableStateFlow<List<VoiceRoom>>(emptyList())
    val voiceRooms: StateFlow<List<VoiceRoom>> = _voiceRooms.asStateFlow()

    // Currently joined active Voice Room
    private val _activeVoiceRoom = MutableStateFlow<VoiceRoom?>(null)
    val activeVoiceRoom: StateFlow<VoiceRoom?> = _activeVoiceRoom.asStateFlow()

    // Direct Messages & Conversations (starts clean)
    private val _messages = MutableStateFlow<List<DirectMessage>>(emptyList())
    val messages: StateFlow<List<DirectMessage>> = _messages.asStateFlow()

    // Notifications (single real welcome notification)
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem(
                id = "n_welcome",
                title = "Welcome to Noise",
                message = "Your social audio & media sharing platform is ready! Upload your profile photo and first post.",
                timeAgo = "Just now",
                avatar = "",
                type = NotificationType.COIN_REWARD
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Actions
    fun toggleLike(postId: String) {
        val uid = _currentUser.value.uid
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val isLiked = post.likedBy.contains(uid)
                    val newLikedBy = if (isLiked) post.likedBy - uid else post.likedBy + uid
                    val newLikes = if (isLiked) (post.likes - 1).coerceAtLeast(0) else post.likes + 1
                    post.copy(likes = newLikes, likedBy = newLikedBy)
                } else post
            }
        }
    }

    fun toggleSave(postId: String) {
        val uid = _currentUser.value.uid
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val isSaved = post.savedBy.contains(uid)
                    val newSavedBy = if (isSaved) post.savedBy - uid else post.savedBy + uid
                    post.copy(savedBy = newSavedBy)
                } else post
            }
        }
    }

    fun addComment(postId: String, text: String) {
        val user = _currentUser.value
        val newComment = CommentItem(
            id = "c_${System.currentTimeMillis()}",
            uid = user.uid,
            username = user.username,
            avatar = user.photoURL,
            text = text,
            createdAt = "Just now"
        )
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(comments = post.comments + newComment)
                } else post
            }
        }
    }

    fun deleteComment(postId: String, commentId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(comments = post.comments.filter { it.id != commentId })
                } else post
            }
        }
    }

    fun createPost(
        image: String,
        caption: String,
        location: String,
        filter: String,
        mediaType: String = "image"
    ) {
        val user = _currentUser.value
        val newPost = Post(
            id = "post-${System.currentTimeMillis()}",
            uid = user.uid,
            username = user.displayName.ifBlank { user.username },
            avatar = user.photoURL,
            image = image,
            caption = caption,
            location = location,
            filter = filter,
            likes = 0,
            likedBy = emptyList(),
            savedBy = emptyList(),
            comments = emptyList(),
            createdAt = System.currentTimeMillis(),
            mediaType = mediaType
        )
        _posts.update { listOf(newPost) + it }
    }

    fun deletePost(postId: String) {
        _posts.update { it.filter { p -> p.id != postId } }
    }

    fun addStory(mediaUrl: String, caption: String) {
        val user = _currentUser.value
        val newStory = Story(
            id = "story-${System.currentTimeMillis()}",
            uid = user.uid,
            username = user.username,
            avatar = user.photoURL,
            mediaUrl = mediaUrl,
            caption = caption,
            timestamp = "Just now",
            hasSeen = false
        )
        _stories.update { listOf(newStory) + it }
    }

    fun deleteStory(storyId: String) {
        _stories.update { it.filter { s -> s.id != storyId } }
    }

    fun markStorySeen(storyId: String) {
        _stories.update { list ->
            list.map { if (it.id == storyId) it.copy(hasSeen = true) else it }
        }
    }

    fun createVoiceRoom(title: String, topic: String, tags: List<String>, ambientVibe: String): VoiceRoom {
        val user = _currentUser.value
        val hostParticipant = RoomParticipant(
            uid = user.uid,
            username = user.username,
            displayName = user.displayName,
            avatar = user.photoURL,
            isHost = true,
            isSpeaker = true,
            isMuted = false,
            isSpeaking = true,
            seatNumber = 0
        )
        val newRoom = VoiceRoom(
            id = "room-${System.currentTimeMillis()}",
            title = title,
            topic = topic,
            hostUid = user.uid,
            hostUsername = user.username,
            hostDisplayName = user.displayName,
            hostAvatar = user.photoURL,
            speakers = listOf(hostParticipant),
            listeners = emptyList(),
            tags = tags,
            ambientVibe = ambientVibe,
            isLive = true,
            createdAt = System.currentTimeMillis(),
            slotLikes = emptyMap()
        )
        _voiceRooms.update { listOf(newRoom) + it }
        _activeVoiceRoom.value = newRoom
        return newRoom
    }

    fun joinVoiceRoom(room: VoiceRoom) {
        val user = _currentUser.value
        val isAlreadySpeaker = room.speakers.any { it.uid == user.uid }
        val isAlreadyListener = room.listeners.any { it.uid == user.uid }

        val updatedRoom = if (!isAlreadySpeaker && !isAlreadyListener) {
            val listener = RoomParticipant(
                uid = user.uid,
                username = user.username,
                displayName = user.displayName,
                avatar = user.photoURL
            )
            room.copy(listeners = room.listeners + listener)
        } else room

        _voiceRooms.update { list -> list.map { if (it.id == updatedRoom.id) updatedRoom else it } }
        _activeVoiceRoom.value = updatedRoom
    }

    fun leaveVoiceRoom() {
        val current = _activeVoiceRoom.value ?: return
        val uid = _currentUser.value.uid
        val updatedRoom = current.copy(
            speakers = current.speakers.filter { it.uid != uid },
            listeners = current.listeners.filter { it.uid != uid }
        )
        _voiceRooms.update { list -> list.map { if (it.id == updatedRoom.id) updatedRoom else it } }
        _activeVoiceRoom.value = null
    }

    fun toggleMuteInRoom() {
        val room = _activeVoiceRoom.value ?: return
        val uid = _currentUser.value.uid
        val updatedSpeakers = room.speakers.map { speaker ->
            if (speaker.uid == uid) {
                speaker.copy(isMuted = !speaker.isMuted, isSpeaking = if (!speaker.isMuted) false else speaker.isSpeaking)
            } else speaker
        }
        val updatedRoom = room.copy(speakers = updatedSpeakers)
        _activeVoiceRoom.value = updatedRoom
        _voiceRooms.update { list -> list.map { if (it.id == room.id) updatedRoom else it } }
    }

    fun requestToSpeak() {
        val room = _activeVoiceRoom.value ?: return
        val user = _currentUser.value
        if (room.speakers.any { it.uid == user.uid }) return

        val nextSeat = (0..5).firstOrNull { seat -> room.speakers.none { it.seatNumber == seat } } ?: return
        val newSpeaker = RoomParticipant(
            uid = user.uid,
            username = user.username,
            displayName = user.displayName,
            avatar = user.photoURL,
            isSpeaker = true,
            isMuted = false,
            isSpeaking = false,
            seatNumber = nextSeat
        )
        val updatedRoom = room.copy(
            listeners = room.listeners.filter { it.uid != user.uid },
            speakers = room.speakers + newSpeaker
        )
        _activeVoiceRoom.value = updatedRoom
        _voiceRooms.update { list -> list.map { if (it.id == room.id) updatedRoom else it } }
    }

    fun sendHeartToSeat(seatIndex: Int) {
        val room = _activeVoiceRoom.value ?: return
        val currentLikes = room.slotLikes[seatIndex] ?: 0
        val updatedSlotLikes = room.slotLikes + (seatIndex to currentLikes + 1)
        val updatedRoom = room.copy(slotLikes = updatedSlotLikes)
        _activeVoiceRoom.value = updatedRoom
        _voiceRooms.update { list -> list.map { if (it.id == room.id) updatedRoom else it } }
    }

    fun toggleFollow(usernameOrUid: String) {
        val key = usernameOrUid.lowercase()
        val current = _followingList.value
        val isFollowing = current.contains(key)
        val updated = if (isFollowing) current - key else current + key
        _followingList.value = updated

        val newFollowingCount = (_currentUser.value.followingCount + if (isFollowing) -1 else 1).coerceAtLeast(0)
        _currentUser.update { it.copy(followingCount = newFollowingCount) }

        prefs.edit()
            .putStringSet("following_list", updated)
            .putInt("following_count", newFollowingCount)
            .apply()
    }

    fun addCoins(amount: Int) {
        val updated = _coinsBalance.value + amount
        _coinsBalance.value = updated
        prefs.edit().putInt("coins_balance", updated).apply()
    }

    fun updateProfile(displayName: String, username: String, bio: String, website: String, avatar: String) {
        _currentUser.update {
            it.copy(
                displayName = displayName,
                username = username,
                bio = bio,
                website = website,
                photoURL = avatar
            )
        }
        prefs.edit()
            .putString("display_name", displayName)
            .putString("username", username)
            .putString("bio", bio)
            .putString("website", website)
            .putString("photo_url", avatar)
            .apply()
    }

    fun updateAvatar(avatarUri: String) {
        _currentUser.update { it.copy(photoURL = avatarUri) }
        prefs.edit().putString("photo_url", avatarUri).apply()
    }

    fun toggleAccountPrivacy(isPrivate: Boolean) {
        _currentUser.update { it.copy(isPrivate = isPrivate) }
        prefs.edit().putBoolean("is_private", isPrivate).apply()
    }

    fun sendMessage(receiverUid: String, text: String) {
        val senderUid = _currentUser.value.uid
        val newMsg = DirectMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderId = senderUid,
            receiverId = receiverUid,
            text = text,
            timestamp = System.currentTimeMillis(),
            isRead = true
        )
        _messages.update { it + newMsg }
    }

    fun blockUser(targetUid: String) {
        _currentUser.update { user ->
            user.copy(blockedUsers = user.blockedUsers + targetUid)
        }
    }
}
