package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun CreateVoiceRoomDialog(
    onDismiss: () -> Unit,
    onCreateRoom: (title: String, topic: String, tags: List<String>, ambientVibe: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var selectedVibe by remember { mutableStateOf("lofi") }
    var selectedTag by remember { mutableStateOf("Music") }

    val vibes = listOf(
        "lofi" to "☕ Lofi Chill",
        "cafe" to "☕ Cozy Cafe",
        "cyber" to "⚡ Cyber Night",
        "acoustic" to "🎸 Acoustic",
        "none" to "🎙️ Just Voice"
    )

    val availableTags = listOf("Music", "Tech", "Lofi", "Design", "Chill", "Storytelling")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(NeonRose.copy(alpha = 0.5f), ElectricPurple.copy(alpha = 0.3f)))
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(NeonRose, ElectricPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "Start Live Audio Room",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextWhite
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                // Title Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Room Name", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("e.g. Midnight Beats & Creative Talk", fontSize = 12.sp, color = TextSubtle) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonRose,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("room_title_input")
                    )
                }

                // Topic Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Topic / Description", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        placeholder = { Text("What are you discussing?", fontSize = 12.sp, color = TextSubtle) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricPurple,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("room_topic_input")
                    )
                }

                // Ambient Audio Vibe Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Ambient Sound Vibe", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        vibes.take(3).forEach { (id, label) ->
                            val isSelected = selectedVibe == id
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) NeonRose.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(
                                        if (isSelected) listOf(NeonRose, ElectricPurple) else listOf(DarkBorder, DarkBorder)
                                    )
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedVibe = id }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) NeonRose else TextMuted,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Tags
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Category Tag", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableTags.take(4).forEach { tag ->
                            val isSelected = selectedTag == tag
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) ElectricPurple.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(
                                        if (isSelected) listOf(ElectricPurple, CyberIndigo) else listOf(DarkBorder, DarkBorder)
                                    )
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTag = tag }
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ElectricPurple else TextMuted,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Go Live Action Button
                Button(
                    onClick = {
                        val roomTitle = title.ifBlank { "Live Studio Session 🎧" }
                        val roomTopic = topic.ifBlank { "Casual Audio Chat & Music" }
                        onCreateRoom(roomTitle, roomTopic, listOf(selectedTag), selectedVibe)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonRose
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_create_room_button")
                ) {
                    Text(
                        text = "Go Live on Stage 🎙️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
