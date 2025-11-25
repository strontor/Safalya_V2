package com.beta.safalya_v2.data.repository

import com.beta.safalya_v2.data.model.AppUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val usersCollection get() = firestore.collection("users")

    suspend fun createOrUpdateUser(user: AppUser) {
        usersCollection.document(user.id).set(user).await()
    }

    suspend fun updateUserRole(userId: String, role: String) {
        usersCollection.document(userId).update("role", role).await()
    }

    suspend fun fetchUser(userId: String): AppUser? {
        val snapshot = usersCollection.document(userId).get().await()
        return snapshot.toObject(AppUser::class.java)?.copy(id = snapshot.id)
    }
}

