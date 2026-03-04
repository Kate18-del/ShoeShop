package com.example.shoeshop.data.model

import Product
import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val id: String,
    val product_id: String,
    val user_id: String,
    val created_at: String? = null
)

@Serializable
data class FavoriteWithProduct(
    val id: String,
    val product: Product,
    val user_id: String,
    val created_at: String? = null
)