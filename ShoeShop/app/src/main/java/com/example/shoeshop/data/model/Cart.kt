package com.example.shoeshop.data.model

import Product
import kotlinx.serialization.Serializable

@Serializable
data class Cart(
    val id: String,
    val product_id: String,
    val user_id: String,
    val count: Int = 1
)

@Serializable
data class CartWithProduct(
    val id: String,
    val product: Product,
    val user_id: String,
    val count: Int = 1
)