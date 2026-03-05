package com.example.shoeshop.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Long = 0,
    val created_at: String = "",
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val user_id: String? = null,
    val payment_id: String? = null,
    val delivery_coast: Double? = 60.20,
    val status_id: String? = null
)

@Serializable
data class OrderItem(
    val id: String = "",
    val created_at: String = "",
    val title: String? = null,
    val coast: Double? = null,
    val count: Int? = null,
    val order_id: Long? = null,
    val product_id: String? = null
)

@Serializable
data class Payment(
    val id: String = "",
    val created_at: String = "",
    val user_id: String? = null,
    val card_name: String? = null,
    val card_number: String? = null
)

