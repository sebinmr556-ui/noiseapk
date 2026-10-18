package com.example.data.util

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

enum class OperationType {
    CREATE,
    UPDATE,
    DELETE,
    LIST,
    GET,
    WRITE
}

data class FirestoreErrorInfo(
    val error: String,
    val authInfo: AuthInfo,
    val operationType: OperationType,
    val path: String?
)

data class AuthInfo(
    val userId: String?,
    val email: String?,
    val emailVerified: Boolean?
)

fun handleFirestoreError(error: Exception, operationType: OperationType, path: String?): FirestoreErrorInfo {
    val user = Firebase.auth.currentUser
    val info = FirestoreErrorInfo(
        error = error.localizedMessage ?: error.message ?: "Unknown error",
        authInfo = AuthInfo(
            userId = user?.uid,
            email = user?.email,
            emailVerified = user?.isEmailVerified
        ),
        operationType = operationType,
        path = path
    )
    Log.e("FirestoreError", "Firestore error on $path: ${info.error}")
    return info
}
