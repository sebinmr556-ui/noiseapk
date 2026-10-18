package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.VideoFile
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
import com.example.ui.theme.*

@Composable
fun UploadScreen(
    currentUser: AppUser,
    onPostCreated: (image: String, caption: String, location: String, filter: String, mediaType: String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMediaUri by remember { mutableStateOf<String?>(null) }
    var mediaType by remember { mutableStateOf("image") } // "image" or "video"
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Normal") }

    val filters = listOf("Normal", "Cyberpunk", "Chrome", "Velvet", "Noir", "Sunset")

    // Launcher for selecting visual media (Images and Videos)
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedMediaUri = uri.toString()
            val uriStr = uri.toString().lowercase()
            mediaType = if (uriStr.contains("video") || uriStr.endsWith(".mp4") || uriStr.endsWith(".mov")) {
                "video"
            } else {
                "image"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar: Cancel & Share
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onCancel, modifier = Modifier.size(28.dp)) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = TextMuted)
            }

            Text(
                text = "New Post",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextWhite
            )

            Button(
                onClick = {
                    val mediaToPost = selectedMediaUri ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=900&q=80"
                    onPostCreated(mediaToPost, caption, location, selectedFilter, mediaType)
                },
                enabled = selectedMediaUri != null || caption.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonRose),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .height(34.dp)
                    .testTag("publish_post_button")
            ) {
                Text(text = "Share", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
            }
        }

        // Upload Area / Media Preview
        if (selectedMediaUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, Brush.linearGradient(listOf(NeonRose, ElectricPurple)), RoundedCornerShape(16.dp))
                    .clickable {
                        mediaPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = selectedMediaUri,
                    contentDescription = "Selected media",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (mediaType == "video") {
                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        contentDescription = "Video",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(54.dp)
                    )
                }

                // Change Photo/Video overlay chip
                Surface(
                    shape = RoundedCornerShape(50),
                    color = ObsidianBlack.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Tap to change",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Filter Tag
                if (selectedFilter != "Normal") {
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
                            text = "✦ $selectedFilter",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        } else {
            // Empty Upload Placeholder with Visual Upload Options
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(NeonRose.copy(alpha = 0.5f), ElectricPurple.copy(alpha = 0.3f)))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        mediaPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    }
                    .testTag("upload_media_picker_trigger")
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(NeonRose.copy(alpha = 0.2f), ElectricPurple.copy(alpha = 0.2f)))
                            )
                            .border(1.5.dp, Brush.linearGradient(listOf(NeonRose, ElectricPurple)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Upload",
                            tint = NeonRose,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Upload Image or Video",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextWhite
                    )
                    Text(
                        text = "Select photos or video files from your device",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                mediaPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Pick Image", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }

                        Button(
                            onClick = {
                                mediaPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = ElectricPurple, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pick Video", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                }
            }
        }

        // Filter Selection
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Aesthetic Filters", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { f ->
                    val isSelected = selectedFilter == f
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isSelected) ElectricPurple.copy(alpha = 0.25f) else DarkSurfaceVariant,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                if (isSelected) listOf(ElectricPurple, CyberIndigo) else listOf(DarkBorder, DarkBorder)
                            )
                        ),
                        modifier = Modifier.clickable { selectedFilter = f }
                    ) {
                        Text(
                            text = f,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ElectricPurple else TextMuted,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Caption Input
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Caption", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("Write a caption, drop tags #beats #sound...", fontSize = 12.sp, color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonRose,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_caption_input")
            )
        }

        // Location Input
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Location (Optional)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("e.g. Soundstage, Tokyo", fontSize = 12.sp, color = TextMuted) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = NeonRose, modifier = Modifier.size(18.dp))
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricPurple,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_location_input")
            )
        }
    }
}
