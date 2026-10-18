package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppUser
import com.example.data.model.Post
import com.example.ui.theme.*

@Composable
fun PostDetailModal(
    post: Post,
    currentUser: AppUser,
    isFollowing: Boolean,
    onClose: () -> Unit,
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
    Dialog(
        onDismissRequest = onClose,
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
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Post Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextWhite
                    )

                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                PostCard(
                    post = post,
                    currentUser = currentUser,
                    isFollowing = isFollowing,
                    onLikeToggle = onLikeToggle,
                    onSaveToggle = onSaveToggle,
                    onCommentClick = onCommentClick,
                    onQuickComment = onQuickComment,
                    onFollowToggle = onFollowToggle,
                    onUserClick = onUserClick,
                    onDeletePost = {
                        onDeletePost()
                        onClose()
                    },
                    onBlockUser = {
                        onBlockUser()
                        onClose()
                    }
                )
            }
        }
    }
}
