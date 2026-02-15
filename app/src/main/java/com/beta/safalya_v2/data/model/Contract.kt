package com.beta.safalya_v2.data.model

data class Contract(
    val id: String = "",
    val itemId: String = "",
    val farmerId: String = "",
    val buyerId: String = "",
    val status: String = "active",
    val createdAt: Long = System.currentTimeMillis()
)
