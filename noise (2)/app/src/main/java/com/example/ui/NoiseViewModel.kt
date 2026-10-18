package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.NoiseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    EXPLORE,
    UPLOAD,
    REELS,
    PROFILE
}

class NoiseViewModel(application: Application) : AndroidViewModel(application) {

    val repository = NoiseRepository(application.applicationContext)

    // Current Navigation Tab
    private val _activeTab = MutableStateFlow(MainTab.HOME)
    val activeTab: StateFlow<MainTab> = _activeTab.asStateFlow()

    // Data streams from repository
    val currentUser = repository.currentUser
    val coinsBalance = repository.coinsBalance
    val followingList = repository.followingList
    val posts = repository.posts
    val stories = repository.stories
    val voiceRooms = repository.voiceRooms
    val activeVoiceRoom = repository.activeVoiceRoom
    val messages = repository.messages
    val notifications = repository.notifications
    val discoverableUsers = repository.discoverableUsers

    // Filtered visible posts (excluding blocked users)
    val visiblePosts = combine(posts, currentUser) { allPosts, user ->
        val blocked = user.blockedUsers
        allPosts.filter { it.uid !in blocked }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Modal & Sheet visibility states
    val isVoiceRoomModalOpen = MutableStateFlow(false)
    val isCreateVoiceRoomOpen = MutableStateFlow(false)
    val isVoiceRoomsListOpen = MutableStateFlow(false)

    val activeStoryIndex = MutableStateFlow<Int?>(null)
    val activeCommentsPost = MutableStateFlow<Post?>(null)
    val selectedPostDetail = MutableStateFlow<Post?>(null)
    val selectedUserProfile = MutableStateFlow<PublicUserProfile?>(null)

    val isDirectMessagesOpen = MutableStateFlow(false)
    val directTargetUser = MutableStateFlow<PublicUserProfile?>(null)

    val isNotificationsOpen = MutableStateFlow(false)
    val isMenuDrawerOpen = MutableStateFlow(false)
    val isCoinStoreOpen = MutableStateFlow(false)
    val isEditProfileOpen = MutableStateFlow(false)
    val isChatroomOpen = MutableStateFlow(false)
    val activeChatroomId = MutableStateFlow("general-lounge")

    fun setTab(tab: MainTab) {
        _activeTab.value = tab
    }

    // Post actions
    fun toggleLike(postId: String) {
        repository.toggleLike(postId)
    }

    fun toggleSave(postId: String) {
        repository.toggleSave(postId)
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        repository.addComment(postId, text.trim())
    }

    fun deleteComment(postId: String, commentId: String) {
        repository.deleteComment(postId, commentId)
    }

    fun createPost(image: String, caption: String, location: String, filter: String, mediaType: String = "image") {
        repository.createPost(image, caption, location, filter, mediaType)
        _activeTab.value = MainTab.HOME
    }

    fun deletePost(postId: String) {
        repository.deletePost(postId)
        if (selectedPostDetail.value?.id == postId) {
            selectedPostDetail.value = null
        }
    }

    // Story actions
    fun addStory(mediaUrl: String, caption: String) {
        repository.addStory(mediaUrl, caption)
    }

    fun deleteStory(storyId: String) {
        repository.deleteStory(storyId)
        activeStoryIndex.value = null
    }

    fun markStorySeen(storyId: String) {
        repository.markStorySeen(storyId)
    }

    // Voice Room actions
    fun createVoiceRoom(title: String, topic: String, tags: List<String>, ambientVibe: String) {
        val room = repository.createVoiceRoom(title, topic, tags, ambientVibe)
        isCreateVoiceRoomOpen.value = false
        isVoiceRoomModalOpen.value = true
    }

    fun joinVoiceRoom(room: VoiceRoom) {
        repository.joinVoiceRoom(room)
        isVoiceRoomModalOpen.value = true
    }

    fun leaveVoiceRoom() {
        repository.leaveVoiceRoom()
        isVoiceRoomModalOpen.value = false
    }

    fun toggleMuteInRoom() {
        repository.toggleMuteInRoom()
    }

    fun requestToSpeak() {
        repository.requestToSpeak()
    }

    fun sendHeartToSeat(seatIndex: Int) {
        repository.sendHeartToSeat(seatIndex)
    }

    // User & Profile actions
    fun toggleFollow(usernameOrUid: String) {
        repository.toggleFollow(usernameOrUid)
    }

    fun addCoins(amount: Int) {
        repository.addCoins(amount)
    }

    fun updateProfile(displayName: String, username: String, bio: String, website: String, avatar: String) {
        repository.updateProfile(displayName, username, bio, website, avatar)
        isEditProfileOpen.value = false
    }

    fun updateAvatar(avatarUri: String) {
        repository.updateAvatar(avatarUri)
    }

    fun toggleAccountPrivacy(isPrivate: Boolean) {
        repository.toggleAccountPrivacy(isPrivate)
    }

    fun openDirectChat(user: PublicUserProfile) {
        directTargetUser.value = user
        isDirectMessagesOpen.value = true
    }

    fun sendMessage(receiverUid: String, text: String) {
        if (text.isBlank()) return
        repository.sendMessage(receiverUid, text.trim())
    }

    fun blockUser(targetUid: String) {
        repository.blockUser(targetUid)
        if (selectedUserProfile.value?.uid == targetUid) {
            selectedUserProfile.value = null
        }
    }
}
