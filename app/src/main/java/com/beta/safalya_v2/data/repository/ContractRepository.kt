package com.beta.safalya_v2.data.repository

import com.beta.safalya_v2.data.model.Contract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ContractsRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun loadContracts(
        isFarmer: Boolean,
        onSuccess: (List<Contract>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onFailure("User not logged in")

        val query = if (isFarmer) {
            db.collection("contracts").whereEqualTo("farmerId", uid)
        } else {
            db.collection("contracts").whereEqualTo("buyerId", uid)
        }

        query.get()
            .addOnSuccessListener { snap ->
                val contracts = snap.documents.map { doc ->
                    Contract(
                        id = doc.id,
                        listingId = doc.getString("listingId") ?: "",
                        farmerId = doc.getString("farmerId") ?: "",
                        buyerId = doc.getString("buyerId") ?: "",
                        status = doc.getString("status") ?: "pending",
                        createdAt = doc.getLong("createdAt") ?: 0
                    )
                }
                onSuccess(contracts)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to fetch contracts")
            }
    }

    fun acceptContract(
        contract: Contract,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("contracts").document(contract.id)
            .update("status", "accepted")
            .addOnSuccessListener {

                // CREATE TRANSACTION
                val tx = hashMapOf(
                    "contractId" to contract.id,
                    "farmerId" to contract.farmerId,
                    "buyerId" to contract.buyerId,
                    "amount" to "0",
                    "type" to "income",
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("transactions")
                    .add(tx)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e.message ?: "Transaction error") }
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Error updating contract") }
    }


    fun rejectContract(
        contract: Contract,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("contracts").document(contract.id)
            .update("status", "rejected")
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Error rejecting contract") }
    }
}
