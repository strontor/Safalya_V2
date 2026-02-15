package com.beta.safalya_v2.data.model

data class Item(
    val id: String = "",
    val farmerId: String = "",
    val cropType: String = "",
    val quantity: String = "",
    val price: String = "",
    val deliveryDate: String = "",
    val description: String = "",
    val status: String = "active",
    val itemType: String = "" // "BUY" or "SELL"
)