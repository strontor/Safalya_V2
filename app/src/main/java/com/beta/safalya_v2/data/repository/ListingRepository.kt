package com.beta.safalya_v2.data.repository

import com.beta.safalya_v2.data.model.Listing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListingRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // -----------------------
    // CREATE LISTING (Farmer)
    // -----------------------
    fun createListing(
        cropType: String,
        quantity: String,
        price: String,
        deliveryDate: String,
        description: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onFailure("User not logged in")

        val data = hashMapOf(
            "farmerId" to uid,
            "cropType" to cropType,
            "quantity" to quantity,
            "price" to price,
            "deliveryDate" to deliveryDate,
            "description" to description,
            "status" to "active"
        )

        db.collection("listings")
            .add(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to add listing") }
    }

    // -----------------------
    // LOAD FARMER LISTINGS
    // -----------------------
    fun loadMyListings(
        onSuccess: (List<Listing>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onFailure("User not logged in")

        db.collection("listings")
            .whereEqualTo("farmerId", uid)
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.map { doc ->
                    Listing(
                        id = doc.id,
                        farmerId = doc.getString("farmerId") ?: "",
                        cropType = doc.getString("cropType") ?: "",
                        quantity = doc.getString("quantity") ?: "",
                        price = doc.getString("price") ?: "",
                        deliveryDate = doc.getString("deliveryDate") ?: "",
                        description = doc.getString("description") ?: "",
                        status = doc.getString("status") ?: "active"
                    )
                }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to fetch") }
    }

    // -----------------------
    // LOAD ACTIVE LISTINGS (Buyers)
    // -----------------------
    fun loadActiveListings(
        onSuccess: (List<Listing>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("listings")
            .whereEqualTo("status", "active")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.map { doc ->
                    Listing(
                        id = doc.id,
                        farmerId = doc.getString("farmerId") ?: "",
                        cropType = doc.getString("cropType") ?: "",
                        quantity = doc.getString("quantity") ?: "",
                        price = doc.getString("price") ?: "",
                        deliveryDate = doc.getString("deliveryDate") ?: "",
                        description = doc.getString("description") ?: "",
                        status = doc.getString("status") ?: "active"
                    )
                }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to fetch") }
    }
    // -----------------------
// REQUEST CONTRACT
// -----------------------
    fun requestContract(
        listingId: String,
        farmerId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val buyerId = auth.currentUser?.uid ?: return onFailure("User not logged in")

        val data = hashMapOf(
            "listingId" to listingId,
            "farmerId" to farmerId,
            "buyerId" to buyerId,
            "timestamp" to System.currentTimeMillis(),
            "status" to "pending"
        )

        db.collection("contract_requests")
            .add(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Request failed") }
    }

}
