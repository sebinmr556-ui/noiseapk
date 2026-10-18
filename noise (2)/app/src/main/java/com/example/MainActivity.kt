package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainTab
import com.example.ui.NoiseViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.NoiseTheme
import com.example.ui.theme.ObsidianBlack
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: NoiseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NoiseTheme {
                val coroutineScope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                // State observers
                val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                val coinsBalance by viewModel.coinsBalance.collectAsStateWithLifecycle()
                val followingList by viewModel.followingList.collectAsStateWithLifecycle()
                val posts by viewModel.posts.collectAsStateWithLifecycle()
                val visiblePosts by viewModel.visiblePosts.collectAsStateWithLifecycle()
                val stories by viewModel.stories.collectAsStateWithLifecycle()
                val voiceRooms by viewModel.voiceRooms.collectAsStateWithLifecycle()
                val activeVoiceRoom by viewModel.activeVoiceRoom.collectAsStateWithLifecycle()
                val messages by viewModel.messages.collectAsStateWithLifecycle()
                val notifications by viewModel.notifications.collectAsStateWithLifecycle()

                // Modals
                val isVoiceRoomModalOpen by viewModel.isVoiceRoomModalOpen.collectAsStateWithLifecycle()
                val isCreateVoiceRoomOpen by viewModel.isCreateVoiceRoomOpen.collectAsStateWithLifecycle()
                val isVoiceRoomsListOpen by viewModel.isVoiceRoomsListOpen.collectAsStateWithLifecycle()
                val activeStoryIndex by viewModel.activeStoryIndex.collectAsStateWithLifecycle()
                val activeCommentsPost by viewModel.activeCommentsPost.collectAsStateWithLifecycle()
                val selectedPostDetail by viewModel.selectedPostDetail.collectAsStateWithLifecycle()
                val selectedUserProfile by viewModel.selectedUserProfile.collectAsStateWithLifecycle()
                val isDirectMessagesOpen by viewModel.isDirectMessagesOpen.collectAsStateWithLifecycle()
                val directTargetUser by viewModel.directTargetUser.collectAsStateWithLifecycle()
                val isNotificationsOpen by viewModel.isNotificationsOpen.collectAsStateWithLifecycle()
                val isMenuDrawerOpen by viewModel.isMenuDrawerOpen.collectAsStateWithLifecycle()
                val isCoinStoreOpen by viewModel.isCoinStoreOpen.collectAsStateWithLifecycle()
                val isEditProfileOpen by viewModel.isEditProfileOpen.collectAsStateWithLifecycle()
                val isChatroomOpen by viewModel.isChatroomOpen.collectAsStateWithLifecycle()
                val activeChatroomId by viewModel.activeChatroomId.collectAsStateWithLifecycle()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianBlack)
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = ObsidianBlack,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            if (activeTab == MainTab.HOME) {
                                NoiseHeader(
                                    coinsBalance = coinsBalance,
                                    hasActiveLiveRooms = voiceRooms.any { it.isLive },
                                    onCoinsClick = { viewModel.isCoinStoreOpen.value = true },
                                    onRadioClick = {
                                        if (activeVoiceRoom != null) {
                                            viewModel.isVoiceRoomModalOpen.value = true
                                        } else {
                                            viewModel.isVoiceRoomsListOpen.value = true
                                        }
                                    },
                                    onNotificationsClick = { viewModel.isNotificationsOpen.value = true },
                                    onDirectMessagesClick = { viewModel.isDirectMessagesOpen.value = true },
                                    onMenuClick = { viewModel.isMenuDrawerOpen.value = true }
                                )
                            }
                        },
                        bottomBar = {
                            Column {
                                // Floating MiniPlayer docked above bottom bar when listening to a live stage
                                if (activeVoiceRoom != null && !isVoiceRoomModalOpen) {
                                    VoiceRoomMiniPlayer(
                                        room = activeVoiceRoom!!,
                                        currentUser = currentUser,
                                        onExpand = { viewModel.isVoiceRoomModalOpen.value = true },
                                        onLeave = { viewModel.leaveVoiceRoom() },
                                        onToggleMute = { viewModel.toggleMuteInRoom() }
                                    )
                                }

                                BottomNavBar(
                                    activeTab = activeTab,
                                    onTabSelected = { tab -> viewModel.setTab(tab) },
                                    currentUser = currentUser
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (activeTab) {
                                MainTab.HOME -> {
                                    FeedScreen(
                                        posts = visiblePosts,
                                        stories = stories,
                                        voiceRooms = voiceRooms,
                                        currentUser = currentUser,
                                        followingList = followingList,
                                        onStoryClick = { index -> viewModel.activeStoryIndex.value = index },
                                        onAddStoryPicked = { mediaUri ->
                                            viewModel.addStory(mediaUri, "My moment ✨")
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Added to your story!")
                                            }
                                        },
                                        onJoinVoiceRoom = { room -> viewModel.joinVoiceRoom(room) },
                                        onCreateVoiceRoomClick = { viewModel.isCreateVoiceRoomOpen.value = true },
                                        onViewAllVoiceRoomsClick = { viewModel.isVoiceRoomsListOpen.value = true },
                                        onLikeToggle = { postId -> viewModel.toggleLike(postId) },
                                        onSaveToggle = { postId -> viewModel.toggleSave(postId) },
                                        onCommentClick = { post -> viewModel.activeCommentsPost.value = post },
                                        onQuickComment = { postId, text ->
                                            viewModel.addComment(postId, text)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Comment added")
                                            }
                                        },
                                        onFollowToggle = { username -> viewModel.toggleFollow(username) },
                                        onUserClick = { uid ->
                                            val target = viewModel.discoverableUsers.find { it.uid == uid }
                                            if (target != null) {
                                                viewModel.selectedUserProfile.value = target
                                            }
                                        },
                                        onDeletePost = { postId -> viewModel.deletePost(postId) },
                                        onBlockUser = { uid ->
                                            viewModel.blockUser(uid)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("User blocked")
                                            }
                                        },
                                        onGoToUpload = { viewModel.setTab(MainTab.UPLOAD) }
                                    )
                                }

                                MainTab.EXPLORE -> {
                                    ExploreScreen(
                                        posts = visiblePosts,
                                        discoverableUsers = viewModel.discoverableUsers,
                                        followingList = followingList,
                                        onFollowToggle = { username -> viewModel.toggleFollow(username) },
                                        onUserClick = { user -> viewModel.selectedUserProfile.value = user },
                                        onSelectPost = { post -> viewModel.selectedPostDetail.value = post }
                                    )
                                }

                                MainTab.UPLOAD -> {
                                    UploadScreen(
                                        currentUser = currentUser,
                                        onPostCreated = { img, caption, loc, filter, mediaType ->
                                            viewModel.createPost(img, caption, loc, filter, mediaType)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Post published to Noise!")
                                            }
                                        },
                                        onCancel = { viewModel.setTab(MainTab.HOME) }
                                    )
                                }

                                MainTab.REELS -> {
                                    ReelsScreen(
                                        posts = visiblePosts,
                                        currentUser = currentUser,
                                        onLikeToggle = { postId -> viewModel.toggleLike(postId) },
                                        onCommentClick = { post -> viewModel.activeCommentsPost.value = post }
                                    )
                                }

                                MainTab.PROFILE -> {
                                    ProfileScreen(
                                        currentUser = currentUser,
                                        posts = posts,
                                        coinsBalance = coinsBalance,
                                        onEditProfileClick = { viewModel.isEditProfileOpen.value = true },
                                        onOpenCoinStore = { viewModel.isCoinStoreOpen.value = true },
                                        onSelectPost = { post -> viewModel.selectedPostDetail.value = post },
                                        onUpdateAvatar = { avatarUri ->
                                            viewModel.updateAvatar(avatarUri)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Profile picture updated!")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Interactive Modals and Dialogs
                    if (activeVoiceRoom != null && isVoiceRoomModalOpen) {
                        VoiceRoomModal(
                            room = activeVoiceRoom!!,
                            currentUser = currentUser,
                            onMinimize = { viewModel.isVoiceRoomModalOpen.value = false },
                            onLeaveRoom = { viewModel.leaveVoiceRoom() },
                            onToggleMute = { viewModel.toggleMuteInRoom() },
                            onRequestToSpeak = { viewModel.requestToSpeak() },
                            onSendHeartToSeat = { seat -> viewModel.sendHeartToSeat(seat) }
                        )
                    }

                    if (isCreateVoiceRoomOpen) {
                        CreateVoiceRoomDialog(
                            onDismiss = { viewModel.isCreateVoiceRoomOpen.value = false },
                            onCreateRoom = { title, topic, tags, vibe ->
                                viewModel.createVoiceRoom(title, topic, tags, vibe)
                            }
                        )
                    }

                    if (isVoiceRoomsListOpen) {
                        VoiceRoomsListDialog(
                            rooms = voiceRooms,
                            onDismiss = { viewModel.isVoiceRoomsListOpen.value = false },
                            onJoinRoom = { room -> viewModel.joinVoiceRoom(room) },
                            onCreateRoomClick = { viewModel.isCreateVoiceRoomOpen.value = true }
                        )
                    }

                    if (activeStoryIndex != null && stories.isNotEmpty()) {
                        StoryViewerModal(
                            stories = stories,
                            initialIndex = activeStoryIndex ?: 0,
                            currentUser = currentUser,
                            onClose = { viewModel.activeStoryIndex.value = null },
                            onStorySeen = { storyId -> viewModel.markStorySeen(storyId) },
                            onDeleteStory = { storyId -> viewModel.deleteStory(storyId) },
                            onOpenVoiceRoom = {
                                viewModel.isVoiceRoomsListOpen.value = true
                            },
                            onSendReply = { reply ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Reply sent: $reply")
                                }
                            }
                        )
                    }

                    if (activeCommentsPost != null) {
                        CommentsBottomSheet(
                            post = activeCommentsPost!!,
                            currentUser = currentUser,
                            onDismiss = { viewModel.activeCommentsPost.value = null },
                            onAddComment = { text -> viewModel.addComment(activeCommentsPost!!.id, text) },
                            onDeleteComment = { commentId ->
                                viewModel.deleteComment(activeCommentsPost!!.id, commentId)
                            }
                        )
                    }

                    if (isDirectMessagesOpen) {
                        DirectMessagesDialog(
                            currentUser = currentUser,
                            messages = messages,
                            discoverableUsers = viewModel.discoverableUsers,
                            initialTargetUser = directTargetUser,
                            onDismiss = {
                                viewModel.isDirectMessagesOpen.value = false
                                viewModel.directTargetUser.value = null
                            },
                            onSendMessage = { receiverUid, text ->
                                viewModel.sendMessage(receiverUid, text)
                            }
                        )
                    }

                    if (isCoinStoreOpen) {
                        CoinStoreDialog(
                            currentCoins = coinsBalance,
                            onDismiss = { viewModel.isCoinStoreOpen.value = false },
                            onPurchaseCoins = { amount ->
                                viewModel.addCoins(amount)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Successfully added $amount Coins! ✨")
                                }
                            }
                        )
                    }

                    if (isMenuDrawerOpen) {
                        MenuDrawer(
                            currentUser = currentUser,
                            coinsBalance = coinsBalance,
                            onDismiss = { viewModel.isMenuDrawerOpen.value = false },
                            onOpenCoinStore = { viewModel.isCoinStoreOpen.value = true },
                            onTogglePrivacy = { isPrivate ->
                                viewModel.toggleAccountPrivacy(isPrivate)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (isPrivate) "Account set to Private" else "Account set to Public"
                                    )
                                }
                            },
                            onEditProfileClick = { viewModel.isEditProfileOpen.value = true },
                            onOpenChatroom = { viewModel.isChatroomOpen.value = true },
                            onSupportClick = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Noise Support: support@noise.app")
                                }
                            }
                        )
                    }

                    if (selectedUserProfile != null) {
                        val isFollowing = followingList.contains(selectedUserProfile!!.username.lowercase()) ||
                                followingList.contains(selectedUserProfile!!.uid.lowercase())
                        val userPosts = posts.filter { it.uid == selectedUserProfile!!.uid }

                        UserProfileDialog(
                            user = selectedUserProfile!!,
                            isFollowing = isFollowing,
                            userPosts = userPosts,
                            onDismiss = { viewModel.selectedUserProfile.value = null },
                            onFollowToggle = { viewModel.toggleFollow(selectedUserProfile!!.username) },
                            onDirectMessageClick = {
                                viewModel.openDirectChat(selectedUserProfile!!)
                            },
                            onSelectPost = { post -> viewModel.selectedPostDetail.value = post }
                        )
                    }

                    if (selectedPostDetail != null) {
                        val post = selectedPostDetail!!
                        val isFollowing = followingList.contains(post.username.lowercase()) ||
                                followingList.contains(post.uid.lowercase())

                        PostDetailModal(
                            post = post,
                            currentUser = currentUser,
                            isFollowing = isFollowing,
                            onClose = { viewModel.selectedPostDetail.value = null },
                            onLikeToggle = { viewModel.toggleLike(post.id) },
                            onSaveToggle = { viewModel.toggleSave(post.id) },
                            onCommentClick = { viewModel.activeCommentsPost.value = post },
                            onQuickComment = { text -> viewModel.addComment(post.id, text) },
                            onFollowToggle = { viewModel.toggleFollow(post.username) },
                            onUserClick = {
                                val target = viewModel.discoverableUsers.find { it.uid == post.uid }
                                if (target != null) {
                                    viewModel.selectedUserProfile.value = target
                                }
                            },
                            onDeletePost = { viewModel.deletePost(post.id) },
                            onBlockUser = { viewModel.blockUser(post.uid) }
                        )
                    }

                    if (isNotificationsOpen) {
                        NotificationsDialog(
                            notifications = notifications,
                            onDismiss = { viewModel.isNotificationsOpen.value = false }
                        )
                    }

                    if (isEditProfileOpen) {
                        EditProfileDialog(
                            currentUser = currentUser,
                            onDismiss = { viewModel.isEditProfileOpen.value = false },
                            onSave = { name, username, bio, website, avatar ->
                                viewModel.updateProfile(name, username, bio, website, avatar)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Profile updated!")
                                }
                            }
                        )
                    }

                    if (isChatroomOpen) {
                        Dialog(
                            onDismissRequest = { viewModel.isChatroomOpen.value = false },
                            properties = DialogProperties(usePlatformDefaultWidth = false)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(ObsidianBlack.copy(alpha = 0.95f))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Chatroom(
                                    roomId = activeChatroomId,
                                    roomTitle = "Noise Community Lounge",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.92f),
                                    onClose = { viewModel.isChatroomOpen.value = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
