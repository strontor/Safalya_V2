package com.beta.safalya_v2.data.model

data class AppUser(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val phone: String = ""
)

enum class UserRole(val value: String) {
    FARMER("farmer"),
    BUYER("buyer");

    companion object {
        fun from(value: String?): UserRole =
            values().firstOrNull { it.value == value } ?: BUYER
    }
}

