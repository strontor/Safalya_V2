package com.beta.safalya_v2.data.model

data class Contract(
    val id: String = "",
    val listingId: String = "",
    val farmerId: String = "",
    val buyerId: String = "",
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis()
)

