package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppUser
import com.example.data.model.Post
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PostCard(
    post: Post,
    currentUser: AppUser,
    isFollowing: Boolean,
    onLikeToggle: () -> Unit,
    onSaveToggle: () -> Unit,
    onCommentClick: () -> Unit,
    onQuickComment: (String) -> Unit,
    onFollowToggle: () -> Unit,
    onUserClick: () -> Unit,
    onDeletePost: () -> Unit,
    onBlockUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLiked = post.likedBy.contains(currentUser.uid)
    val isSaved = post.savedBy.contains(currentUser.uid)
    val isMyPost = post.uid == currentUser.uid

    var showMenu by remember { mutableStateOf(false) }
    var quickCommentText by remember { mutableStateOf("") }
    var showHeartBurst by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Like scale bounce animation
    val likeScale by animateFloatAsState(
        targetValue = if (isLiked) 1.25f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "likeBounce"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder.copy(alpha = 0.5f)))
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("post_card_${post.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // User Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .clickable(onClick = onUserClick)
                        .testTag("post_author_${post.id}")
                ) {
                    AsyncImage(
                        model = post.avatar,
                        contentDescription = post.username,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Brush.linearGradient(listOf(NeonRose, ElectricPurple)), CircleShape)
                    )

                    Column {
                        Text(
                            text = post.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextWhite
                        )
                        if (post.location.isNotBlank()) {
                            Text(
                                text = post.location,
                                fontSize = 11.sp,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isMyPost) {
                        Button(
                            onClick = onFollowToggle,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowing) DarkSurfaceVariant else NeonRose
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("follow_button_${post.id}")
                        ) {
                            Text(
                                text = if (isFollowing) "Following" else "Follow",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFollowing) TextWhite else Color.White
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(DarkSurfaceElevated)
                        ) {
                            if (isMyPost) {
                                DropdownMenuItem(
                                    text = { Text("Delete Post", color = MutedRed) },
                                    onClick = {
                                        showMenu = false
                                        onDeletePost()
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Block User", color = MutedRed) },
                                    onClick = {
                                        showMenu = false
                                        onBlockUser()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Post Image with Double-Tap Gesture & Animated Heart Burst
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (!isLiked) onLikeToggle()
                                coroutineScope.launch {
                                    showHeartBurst = true
                                    delay(650)
                                    showHeartBurst = false
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = post.image,
                    contentDescription = post.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Filter Badge Tag
                if (post.filter.isNotBlank() && post.filter != "Normal") {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = ObsidianBlack.copy(alpha = 0.75f),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(NeonRose, ElectricPurple))
                        ),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "✦ ${post.filter}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Heart Burst Animation on Double Tap
                androidx.compose.animation.AnimatedVisibility(
                    visible = showHeartBurst,
                    enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Liked",
                        tint = NeonRose,
                        modifier = Modifier
                            .size(100.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .background(ObsidianBlack.copy(alpha = 0.4f), CircleShape)
                            .padding(16.dp)
                    )
                }
            }

            // Action Row: Like, Comment, Share, Save
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onLikeToggle,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("like_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isLiked) "Unlike" else "Like",
                            tint = if (isLiked) NeonRose else TextWhite,
                            modifier = Modifier
                                .size(24.dp)
                                .scale(likeScale)
                        )
                    }

                    IconButton(
                        onClick = onCommentClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("comment_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = TextWhite,
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    IconButton(
                        onClick = {},
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = TextWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("save_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isSaved) "Remove Bookmark" else "Save Post",
                        tint = if (isSaved) CoinGold else TextWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Likes Counter & Caption
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${post.likes} likes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextWhite
                )

                if (post.caption.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${post.username}  ${post.caption}",
                        fontSize = 12.sp,
                        color = TextWhite,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (post.comments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "View all ${post.comments.size} comments",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.clickable(onClick = onCommentClick)
                    )
                }
            }

            // Inline Quick Comment Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = currentUser.photoURL,
                    contentDescription = "My avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )

                OutlinedTextField(
                    value = quickCommentText,
                    onValueChange = { quickCommentText = it },
                    placeholder = { Text("Add a comment...", fontSize = 11.sp, color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(50),
                    trailingIcon = {
                        if (quickCommentText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    onQuickComment(quickCommentText)
                                    quickCommentText = ""
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send Comment",
                                    tint = NeonRose,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                )
            }
        }
    }
}
