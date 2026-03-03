package com.example.shoeshop.data.model

data class ChangePasswordResponse(
    val id: String? = null,
    val email: String? = null,
    val aud: String? = null,
    val role: String? = null,
    val phone: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val app_metadata: Map<String, Any>? = null,
    val user_metadata: Map<String, Any>? = null
)
