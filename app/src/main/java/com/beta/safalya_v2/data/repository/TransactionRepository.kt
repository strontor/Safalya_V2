package com.beta.safalya_v2.data.repository

import com.beta.safalya_v2.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TransactionRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // -----------------------------------------
    // CREATE TRANSACTION WHEN FARMER ACCEPTS
    // -----------------------------------------
    fun createTransaction(
        contractId: String,
        buyerId: String,
        amount: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val farmerId = auth.currentUser?.uid ?: return onFailure("User not logged in")

        val data = hashMapOf(
            "farmerId" to farmerId,
            "buyerId" to buyerId,
            "contractId" to contractId,
            "amount" to amount,
            "type" to "income",
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("transactions")
            .add(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Could not create transaction") }
    }

    // -----------------------------------------
    // FETCH TRANSACTIONS (Both Roles)
    // -----------------------------------------
    fun loadTransactions(
        isFarmer: Boolean,
        onSuccess: (List<Transaction>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onFailure("User not logged in")

        val query = if (isFarmer) {
            db.collection("transactions").whereEqualTo("farmerId", uid)
        } else {
            db.collection("transactions").whereEqualTo("buyerId", uid)
        }

        query.get().addOnSuccessListener { snap ->
            val list = snap.documents.map { doc ->
                Transaction(
                    id = doc.id,
                    farmerId = doc.getString("farmerId") ?: "",
                    buyerId = doc.getString("buyerId") ?: "",
                    contractId = doc.getString("contractId") ?: "",
                    amount = doc.getString("amount") ?: "0",
                    type = doc.getString("type") ?: "income",
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
            }
            onSuccess(list)
        }.addOnFailureListener { e ->
            onFailure(e.message ?: "Failed to load transactions")
        }
    }
}
