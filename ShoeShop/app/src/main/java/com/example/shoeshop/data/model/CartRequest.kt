package com.example.shoeshop.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddToCartRequest(
    val user_id: String,
    val product_id: String,
    val count: Int
)

@Serializable
data class UpdateCartRequest(
    val count: Int
)