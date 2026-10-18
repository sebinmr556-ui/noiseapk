package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.ChatMessage
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class ChatroomRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun getCurrentUser() = auth.currentUser

    fun observeMessages(roomId: String): Flow<List<ChatMessage>> {
        val path = "chatrooms/$roomId/messages"
        return db.collection("chatrooms")
            .document(roomId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
                }
            }
            .catch { exception ->
                if (exception is Exception) {
                    handleFirestoreError(exception, OperationType.LIST, path)
                }
                emit(emptyList())
            }
    }

    suspend fun sendMessage(roomId: String, text: String): Result<Unit> {
        val user = auth.currentUser
            ?: return Result.failure(IllegalStateException("User must be signed in to send messages"))

        val path = "chatrooms/$roomId/messages"
        val payload = hashMapOf<String, Any>(
            "roomId" to roomId,
            "senderId" to user.uid,
            "senderName" to (user.displayName ?: "User"),
            "senderAvatar" to (user.photoUrl?.toString() ?: ""),
            "text" to text.trim(),
            "createdAt" to FieldValue.serverTimestamp()
        )

        return try {
            db.collection("chatrooms")
                .document(roomId)
                .collection("messages")
                .add(payload)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }
}
