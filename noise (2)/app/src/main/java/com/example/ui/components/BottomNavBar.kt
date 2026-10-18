package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.AppUser
import com.example.ui.MainTab
import com.example.ui.theme.*

@Composable
fun BottomNavBar(
    activeTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    currentUser: AppUser,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ObsidianBlack,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Home
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTabSelected(MainTab.HOME) }
                    .testTag("nav_tab_home"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (activeTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = if (activeTab == MainTab.HOME) NeonRose else TextMuted,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Explore / Search
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTabSelected(MainTab.EXPLORE) }
                    .testTag("nav_tab_explore"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (activeTab == MainTab.EXPLORE) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Explore",
                    tint = if (activeTab == MainTab.EXPLORE) ElectricPurple else TextMuted,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Upload (+) Button with Neon Glow Gradient
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTabSelected(MainTab.UPLOAD) }
                    .testTag("nav_tab_upload"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(listOf(NeonRose, ElectricPurple, CyberIndigo))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Post",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Reels
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTabSelected(MainTab.REELS) }
                    .testTag("nav_tab_reels"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (activeTab == MainTab.REELS) Icons.Filled.Movie else Icons.Outlined.Movie,
                    contentDescription = "Reels",
                    tint = if (activeTab == MainTab.REELS) NeonRoseLight else TextMuted,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Profile
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTabSelected(MainTab.PROFILE) }
                    .testTag("nav_tab_profile"),
                contentAlignment = Alignment.Center
            ) {
                if (currentUser.photoURL.isNotBlank()) {
                    AsyncImage(
                        model = currentUser.photoURL,
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (activeTab == MainTab.PROFILE) 2.dp else 1.dp,
                                color = if (activeTab == MainTab.PROFILE) NeonRose else TextMuted,
                                shape = CircleShape
                            )
                    )
                } else {
                    Icon(
                        imageVector = if (activeTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = if (activeTab == MainTab.PROFILE) NeonRose else TextMuted,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
