package com.example.shoeshop.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val user_id: String,
    val email: String,
    val phone: String,
    val address: String,
    val delivery_coast: Int,
    val status_id: String
)

@Serializable
data class CreateOrderItemRequest(
    val order_id: Long,
    val product_id: String,
    val title: String,
    val coast: Double,
    val count: Int
)