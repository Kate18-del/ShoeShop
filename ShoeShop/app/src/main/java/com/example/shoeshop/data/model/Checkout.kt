package com.example.shoeshop.data.model

import kotlinx.serialization.Serializable



@Serializable
data class Payment(
    val id: String = "",
    val created_at: String = "",
    val user_id: String? = null,
    val card_name: String? = null,
    val card_number: String? = null
)

