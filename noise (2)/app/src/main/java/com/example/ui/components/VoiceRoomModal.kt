package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.AppUser
import com.example.data.model.VoiceRoom
import com.example.ui.theme.*

@Composable
fun VoiceRoomModal(
    room: VoiceRoom,
    currentUser: AppUser,
    onMinimize: () -> Unit,
    onLeaveRoom: () -> Unit,
    onToggleMute: () -> Unit,
    onRequestToSpeak: () -> Unit,
    onSendHeartToSeat: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isCurrentSpeaker = room.speakers.any { it.uid == currentUser.uid }
    val currentSpeakerParticipant = room.speakers.find { it.uid == currentUser.uid }
    val isMuted = currentSpeakerParticipant?.isMuted ?: true
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Stage, 1: Live Chat

    // Infinite speaking wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "speakingWave")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveScale"
    )

    Dialog(
        onDismissRequest = onMinimize,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ObsidianBlack,
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Bar: Minimize, Room Title, Leave
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onMinimize,
                        modifier = Modifier.testTag("minimize_voice_room_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize Room",
                            tint = TextWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = NeonRose.copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(NeonRose, ElectricPurple))
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(LiveGreen)
                                )
                                Text(
                                    text = "LIVE AUDIO STAGE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonRose
                                )
                            }
                        }
                        Text(
                            text = room.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = onLeaveRoom,
                        modifier = Modifier.testTag("leave_voice_room_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Leave Room",
                            tint = MutedRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Ambient Audio Vibe & Topic Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceVariant,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(DarkBorder, ElectricPurple.copy(alpha = 0.3f)))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = room.topic,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                room.tags.forEach { tag ->
                                    Text(
                                        text = "#$tag",
                                        fontSize = 10.sp,
                                        color = ElectricPurple,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Ambient Sound tag
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = ObsidianBlack
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = CoinGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = when (room.ambientVibe) {
                                        "lofi" -> "Lofi Beats"
                                        "cafe" -> "Cozy Cafe"
                                        "cyber" -> "Cyber City"
                                        "acoustic" -> "Acoustic"
                                        else -> "None"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoinGold
                                )
                            }
                        }
                    }
                }

                // Stage vs Live Chatroom Toggle Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (selectedSubTab == 0) NeonRose else DarkSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSubTab = 0 }
                    ) {
                        Text(
                            text = "🎙️ Stage Slots",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedSubTab == 0) Color.White else TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (selectedSubTab == 1) NeonRose else DarkSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSubTab = 1 }
                    ) {
                        Text(
                            text = "💬 Live Stage Chat",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedSubTab == 1) Color.White else TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }

                if (selectedSubTab == 0) {
                    // Stage Grid: 6 Speaker Slots (2 rows of 3)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                    for (row in 0..1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0..2) {
                                val seatIndex = row * 3 + col
                                val speaker = room.speakers.find { it.seatNumber == seatIndex }
                                val seatLikes = room.slotLikes[seatIndex] ?: 0

                                if (speaker != null) {
                                    // Occupied Speaker Seat
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.testTag("speaker_seat_$seatIndex")
                                    ) {
                                        Box(
                                            modifier = Modifier.size(76.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            // Glowing audio waves when speaking
                                            if (speaker.isSpeaking) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(76.dp)
                                                        .scale(waveScale)
                                                        .clip(CircleShape)
                                                        .background(NeonRose.copy(alpha = 0.25f))
                                                        .border(2.dp, NeonRose, CircleShape)
                                                )
                                            }

                                            AsyncImage(
                                                model = speaker.avatar,
                                                contentDescription = speaker.displayName,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(62.dp)
                                                    .clip(CircleShape)
                                                    .border(
                                                        width = 2.dp,
                                                        color = if (speaker.isSpeaking) NeonRose else if (speaker.isHost) CoinGold else DarkBorder,
                                                        shape = CircleShape
                                                    )
                                            )

                                            // Mic Mute / Speaking Badge
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(if (speaker.isMuted) MutedRed else LiveGreen)
                                                    .border(1.5.dp, ObsidianBlack, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (speaker.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                            }

                                            // Host Crown Badge
                                            if (speaker.isHost) {
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.TopStart)
                                                        .size(18.dp)
                                                        .clip(CircleShape)
                                                        .background(CoinGold),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("👑", fontSize = 9.sp)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = speaker.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        // Slot Reaction Hearts Button
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = DarkSurfaceElevated,
                                            modifier = Modifier
                                                .padding(top = 2.dp)
                                                .clickable { onSendHeartToSeat(seatIndex) }
                                                .testTag("seat_heart_button_$seatIndex")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Favorite,
                                                    contentDescription = "Send Love",
                                                    tint = NeonRose,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Text(
                                                    text = "$seatLikes",
                                                    fontSize = 10.sp,
                                                    color = TextWhite,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    // Empty Speaker Seat
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clickable { onRequestToSpeak() }
                                            .testTag("empty_seat_$seatIndex")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(62.dp)
                                                .clip(CircleShape)
                                                .background(DarkSurfaceVariant)
                                                .border(1.dp, DarkBorder, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Take Seat",
                                                tint = TextMuted,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Empty",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Live Stage Chatroom powered by Firestore
                    Chatroom(
                        roomId = room.id,
                        roomTitle = "${room.title} Live Chat",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClose = { selectedSubTab = 0 }
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "AUDIENCE (${room.listeners.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    LazyRow(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(room.listeners) { listener ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(
                                    model = listener.avatar,
                                    contentDescription = listener.displayName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, DarkBorder, CircleShape)
                                )
                                Text(
                                    text = listener.displayName,
                                    fontSize = 10.sp,
                                    color = TextWhite,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Bottom Controls: Mic Mute, Raise Hand / Speak, Leave
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = DarkSurface,
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isCurrentSpeaker) {
                            Button(
                                onClick = onToggleMute,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMuted) MutedRed else LiveGreen
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.testTag("toggle_mic_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (isMuted) "Unmute" else "Muted",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = onRequestToSpeak,
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.testTag("raise_hand_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PanTool,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Raise Hand",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Leave Room
                        OutlinedButton(
                            onClick = onLeaveRoom,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedRed),
                            border = ButtonDefaults.outlinedButtonBorder().copy(
                                brush = Brush.linearGradient(listOf(MutedRed.copy(alpha = 0.5f), MutedRed.copy(alpha = 0.5f)))
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("leave_stage_button")
                        ) {
                            Text(
                                text = "Leave Quietly",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
