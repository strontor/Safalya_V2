package com.beta.safalya_v2.data.repository

import com.beta.safalya_v2.data.model.Item
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
        itemType: String,
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
            "status" to "active",
            "itemType" to itemType
        )

        db.collection("listings")
            .add(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Failed to add listing") }
    }

    // -----------------------
    // LOAD FARMER LISTINGS
    // -----------------------
    fun loadActiveSellListings(
        onSuccess: (List<Item>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("listings")
            .whereEqualTo("status", "active")
            .whereEqualTo("itemType", "SELL")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.map { doc ->
                    Item(
                        id = doc.id,
                        farmerId = doc.getString("farmerId") ?: "",
                        cropType = doc.getString("cropType") ?: "",
                        quantity = doc.getString("quantity") ?: "",
                        price = doc.getString("price") ?: "",
                        deliveryDate = doc.getString("deliveryDate") ?: "",
                        description = doc.getString("description") ?: "",
                        status = doc.getString("status") ?: "active",
                        itemType = doc.getString("itemType") ?: ""
                    )
                }
                onSuccess(list)
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to load sell listings") }
    }


    // -----------------------
    // LOAD ACTIVE LISTINGS (Buyers)
    // -----------------------
    fun loadActiveBuyOrders(
        onSuccess: (List<Item>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("listings")
            .whereEqualTo("status", "active")
            .whereEqualTo("itemType", "BUY")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.map { doc ->
                    Item(
                        id = doc.id,
                        farmerId = doc.getString("farmerId") ?: "",
                        cropType = doc.getString("cropType") ?: "",
                        quantity = doc.getString("quantity") ?: "",
                        price = doc.getString("price") ?: "",
                        deliveryDate = doc.getString("deliveryDate") ?: "",
                        description = doc.getString("description") ?: "",
                        status = doc.getString("status") ?: "active",
                        itemType = doc.getString("itemType") ?: ""
                    )
                }
                onSuccess(list)
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to load buy orders") }
    }

    fun loadMyListingsByType(
        itemType: String,
        onSuccess: (List<Item>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onFailure("User not logged in")

        db.collection("listings")
            .whereEqualTo("farmerId", uid)
            .whereEqualTo("status", "active")
            .whereEqualTo("itemType", itemType)
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.map { doc ->
                    Item(
                        id = doc.id,
                        farmerId = doc.getString("farmerId") ?: "",
                        cropType = doc.getString("cropType") ?: "",
                        quantity = doc.getString("quantity") ?: "",
                        price = doc.getString("price") ?: "",
                        deliveryDate = doc.getString("deliveryDate") ?: "",
                        description = doc.getString("description") ?: "",
                        status = doc.getString("status") ?: "active",
                        itemType = doc.getString("itemType") ?: ""
                    )
                }
                onSuccess(list)
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to load your listings") }
    }

    // -----------------------
// REQUEST CONTRACT
// -----------------------
    fun requestContract(
        itemId: String,
        farmerId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val buyerId = auth.currentUser?.uid ?: return onFailure("User not logged in")

        val data = hashMapOf(
            "itemId" to itemId,
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

    fun loadInterestedBuyers(
        itemId: String,
        onSuccess: (List<Pair<String, String>>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("contract_requests")
            .whereEqualTo("itemId", itemId)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { snap ->
                val buyers = snap.documents.map {
                    it.id to (it.getString("buyerId") ?: "")
                }
                onSuccess(buyers)
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Failed to load buyers")
            }
    }

    fun acceptBuyer(
        itemId: String,
        acceptedRequestId: String,
        farmerId: String,
        buyerId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val batch = db.batch()

        val requestsRef = db.collection("contract_requests")
        val listingRef = db.collection("listings").document(itemId)
        val contractRef = db.collection("contracts").document()

        // 1️⃣ Accept chosen request
        batch.update(
            requestsRef.document(acceptedRequestId),
            "status", "accepted"
        )

        // 2️⃣ Reject all other requests for this listing
        db.collection("contract_requests")
            .whereEqualTo("itemId", itemId)
            .get()
            .addOnSuccessListener { snap ->

                snap.documents.forEach { doc ->
                    if (doc.id != acceptedRequestId) {
                        batch.update(doc.reference, "status", "rejected")
                    }
                }

                // 3️⃣ Create contract
                val contractData = hashMapOf(
                    "itemId" to itemId,
                    "farmerId" to farmerId,
                    "buyerId" to buyerId,
                    "status" to "active",
                    "createdAt" to System.currentTimeMillis()
                )
                batch.set(contractRef, contractData)

                // 4️⃣ Close listing
                batch.update(listingRef, "status", "closed")

                // 🔥 COMMIT EVERYTHING
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener {
                        onFailure(it.message ?: "Failed to create contract")
                    }
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Failed to process requests")
            }
    }

    fun getBuyerRequestStatus(
        itemId: String,
        buyerId: String,
        onResult: (String?) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("contract_requests")
            .whereEqualTo("itemId", itemId)
            .whereEqualTo("buyerId", buyerId)
            .limit(1)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    onResult(null) // no request yet
                } else {
                    onResult(snap.documents[0].getString("status"))
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Failed to check request status")
            }
    }
}
