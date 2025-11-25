package com.beta.safalya_v2.data.model

data class Transaction(
    val id: String = "",
    val farmerId: String = "",
    val buyerId: String = "",
    val contractId: String = "",
    val amount: String = "0",
    val type: String = "income",
    val createdAt: Long = System.currentTimeMillis()
)
