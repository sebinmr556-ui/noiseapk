package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun NoiseHeader(
    coinsBalance: Int,
    hasActiveLiveRooms: Boolean,
    onCoinsClick: () -> Unit,
    onRadioClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onDirectMessagesClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for Live Voice Rooms radio icon
    val infiniteTransition = rememberInfiniteTransition(label = "radioPulse")
    val radioScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Surface(
        color = ObsidianBlack,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Name & Coin Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Noise",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(NeonRose, ElectricPurple, CyberIndigo)
                        )
                    ),
                    modifier = Modifier.testTag("app_brand_title")
                )

                // Coins Chip
                Surface(
                    shape = RoundedCornerShape(50),
                    color = DarkSurfaceVariant,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(CoinGold.copy(alpha = 0.5f), DarkBorder))
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onCoinsClick)
                        .testTag("coins_header_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(CoinGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "¢",
                                color = ObsidianBlack,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "$coinsBalance",
                            color = CoinGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Right Action Icons: Live Radio, Notifications, Direct Messages, Menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Live Voice Rooms / Radio Button
                IconButton(
                    onClick = onRadioClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("voice_rooms_header_button")
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = Icons.Filled.Radio,
                            contentDescription = "Live Voice Chatrooms",
                            tint = NeonRose,
                            modifier = Modifier
                                .size(24.dp)
                                .scale(if (hasActiveLiveRooms) radioScale else 1f)
                        )
                        if (hasActiveLiveRooms) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(LiveGreen)
                            )
                        }
                    }
                }

                // Notifications Button
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("notifications_header_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Notifications",
                        tint = TextWhite,
                        modifier = Modifier.size(23.dp)
                    )
                }

                // Direct Messages with unread badge
                IconButton(
                    onClick = onDirectMessagesClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("direct_messages_header_button")
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Direct Messages",
                            tint = TextWhite,
                            modifier = Modifier.size(23.dp)
                        )
                        Box(
                            modifier = Modifier
                                .offset(x = 4.dp, y = (-2).dp)
                                .clip(CircleShape)
                                .background(NeonRose)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "2",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 3-Line Hamburger Menu
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("menu_header_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Settings Menu",
                        tint = TextWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
