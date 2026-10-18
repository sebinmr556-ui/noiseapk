package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppUser
import com.example.data.model.Post
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    currentUser: AppUser,
    posts: List<Post>,
    coinsBalance: Int,
    onEditProfileClick: () -> Unit,
    onOpenCoinStore: () -> Unit,
    onSelectPost: (Post) -> Unit,
    onUpdateAvatar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Posts, 1: Saved

    val myPosts = remember(posts, currentUser.uid) {
        posts.filter { it.uid == currentUser.uid }
    }

    val savedPosts = remember(posts, currentUser.uid) {
        posts.filter { it.savedBy.contains(currentUser.uid) }
    }

    // Photo picker launcher for profile picture
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdateAvatar(uri.toString())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .padding(bottom = 60.dp)
            .testTag("profile_screen")
    ) {
        // Profile Header Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Username and Privacy indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = currentUser.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextWhite
                )
                if (currentUser.isVerified) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified Creator",
                        tint = NeonRose,
                        modifier = Modifier.size(17.dp)
                    )
                }
                if (currentUser.isPrivate) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Private Account",
                        tint = CyberIndigo,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Avatar & Stats Row with Photo Picker Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, Brush.linearGradient(listOf(NeonRose, ElectricPurple, CyberIndigo)), CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("profile_avatar_upload_trigger"),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentUser.photoURL.isNotBlank()) {
                        AsyncImage(
                            model = currentUser.photoURL,
                            contentDescription = currentUser.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkSurfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Add Avatar",
                                tint = TextMuted,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }

                    // Camera badge overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(NeonRose)
                            .border(1.5.dp, ObsidianBlack, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${myPosts.size}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextWhite)
                        Text(text = "Posts", fontSize = 11.sp, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${currentUser.followersCount}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextWhite)
                        Text(text = "Followers", fontSize = 11.sp, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${currentUser.followingCount}", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextWhite)
                        Text(text = "Following", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Display Name, Bio, Website
            Text(
                text = currentUser.displayName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextWhite
            )
            if (currentUser.bio.isNotBlank()) {
                Text(
                    text = currentUser.bio,
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            if (currentUser.website.isNotBlank()) {
                Text(
                    text = currentUser.website,
                    fontSize = 12.sp,
                    color = ElectricPurple,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Edit Profile, Upload Photo, & Coins Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEditProfileClick,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder))
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("edit_profile_button")
                ) {
                    Text(text = "Edit Profile", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextWhite)
                }

                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = Brush.linearGradient(listOf(NeonRose.copy(alpha = 0.5f), DarkBorder))
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("upload_profile_photo_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = NeonRose,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Photo", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextWhite)
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceVariant,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(CoinGold.copy(alpha = 0.5f), DarkBorder))
                    ),
                    modifier = Modifier
                        .clickable(onClick = onOpenCoinStore)
                        .height(38.dp)
                        .testTag("profile_coins_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Coins",
                            tint = CoinGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$coinsBalance",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoinGold
                        )
                    }
                }
            }
        }

        // Segmented Tabs: Posts vs Saved
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = 0 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GridOn,
                    contentDescription = "Posts Grid",
                    tint = if (selectedTab == 0) NeonRose else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedTab = 1 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Saved Posts",
                    tint = if (selectedTab == 1) CoinGold else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Posts Grid
        val displayList = if (selectedTab == 0) myPosts else savedPosts

        if (displayList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (selectedTab == 0) "No posts shared yet" else "No saved posts",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (selectedTab == 0) "Upload your first photo or video moment!" else "Save moments to view them here",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayList) { post ->
                    AsyncImage(
                        model = post.image,
                        contentDescription = post.caption,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { onSelectPost(post) }
                            .testTag("profile_post_grid_item_${post.id}")
                    )
                }
            }
        }
    }
}
