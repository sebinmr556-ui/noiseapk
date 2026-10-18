package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppUser
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.VoiceRoom
import com.example.ui.components.LiveVoiceRoomsBar
import com.example.ui.components.PostCard
import com.example.ui.components.StoriesBar
import com.example.ui.theme.*

@Composable
fun FeedScreen(
    posts: List<Post>,
    stories: List<Story>,
    voiceRooms: List<VoiceRoom>,
    currentUser: AppUser,
    followingList: Set<String>,
    onStoryClick: (Int) -> Unit,
    onAddStoryPicked: (mediaUri: String) -> Unit,
    onJoinVoiceRoom: (VoiceRoom) -> Unit,
    onCreateVoiceRoomClick: () -> Unit,
    onViewAllVoiceRoomsClick: () -> Unit,
    onLikeToggle: (String) -> Unit,
    onSaveToggle: (String) -> Unit,
    onCommentClick: (Post) -> Unit,
    onQuickComment: (postId: String, text: String) -> Unit,
    onFollowToggle: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    onBlockUser: (String) -> Unit,
    onGoToUpload: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Launcher for selecting story photo or video from device
    val storyPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onAddStoryPicked(uri.toString())
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .testTag("feed_lazy_column"),
        contentPadding = PaddingValues(bottom = 72.dp)
    ) {
        // Stories Row with Photo/Video Picker Trigger
        item {
            StoriesBar(
                stories = stories,
                currentUser = currentUser,
                onStoryClick = onStoryClick,
                onAddStoryClick = {
                    storyPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )
                }
            )
        }

        // Live Voice Rooms Carousel
        item {
            LiveVoiceRoomsBar(
                rooms = voiceRooms,
                onJoinRoom = onJoinVoiceRoom,
                onCreateRoomClick = onCreateVoiceRoomClick,
                onViewAllRoomsClick = onViewAllVoiceRoomsClick
            )
            Divider(color = DarkBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
        }

        // Feed Posts
        if (posts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "No posts yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextWhite
                        )
                        Text(
                            text = "Be the first to share an audio moment, photo, or video clip!",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )
                        Button(
                            onClick = onGoToUpload,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonRose),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                        ) {
                            Text("Create First Post ✦", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
                        }
                    }
                }
            }
        } else {
            items(posts, key = { it.id }) { post ->
                val isFollowing = followingList.contains(post.username.lowercase()) ||
                        followingList.contains(post.uid.lowercase())

                PostCard(
                    post = post,
                    currentUser = currentUser,
                    isFollowing = isFollowing,
                    onLikeToggle = { onLikeToggle(post.id) },
                    onSaveToggle = { onSaveToggle(post.id) },
                    onCommentClick = { onCommentClick(post) },
                    onQuickComment = { text -> onQuickComment(post.id, text) },
                    onFollowToggle = { onFollowToggle(post.username) },
                    onUserClick = { onUserClick(post.uid) },
                    onDeletePost = { onDeletePost(post.id) },
                    onBlockUser = { onBlockUser(post.uid) }
                )
            }
        }
    }
}
