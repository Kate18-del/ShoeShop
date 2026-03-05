package com.example.shoeshop.data.repository

import android.util.Log
import com.example.shoeshop.data.CartItem
import com.example.shoeshop.data.RetrofitInstance
import com.example.shoeshop.data.model.Order
import com.example.shoeshop.data.model.OrderItem
import com.example.shoeshop.data.model.Payment

class CheckoutRepository {

    private val service = RetrofitInstance.userManagementService
    private val tag = "CheckoutRepository"

    // Получить платежные методы пользователя
    suspend fun getUserPayments(userId: String, token: String): List<Payment>? {
        return try {
            Log.d(tag, "Getting payments for user: $userId")
            val filter = "eq.$userId"
            val response = service.getPayments(filter, "Bearer $token")

            if (response.isSuccessful) {
                val payments = response.body()
                Log.d(tag, "Payments loaded: ${payments?.size}")
                payments
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in getUserPayments", e)
            null
        }
    }

    // Создать заказ
    suspend fun createOrder(
        userId: String,
        email: String,
        phone: String,
        address: String,
        totalAmount: Double,
        deliveryCost: Double,
        token: String
    ): Order? {
        return try {
            Log.d(tag, "Creating order for user: $userId")

            val order = mapOf(
                "user_id" to userId,
                "email" to email,
                "phone" to phone,
                "address" to address,
                "delivery_coast" to deliveryCost,
                "status_id" to "970aed1e-549c-499b-a649-4bf3f9f93a01" // "Собираем"
            )

            val response = service.createOrder(order, "Bearer $token")

            if (response.isSuccessful) {
                val created = response.body()
                Log.d(tag, "Order created successfully")
                created?.firstOrNull()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in createOrder", e)
            null
        }
    }

    // Создать позиции заказа
    suspend fun createOrderItems(orderId: Long, cartItems: List<CartItem>, token: String): Boolean {
        return try {
            Log.d(tag, "Creating order items for order: $orderId")

            val orderItems = cartItems.map { cartItem ->
                mapOf(
                    "order_id" to orderId,
                    "product_id" to cartItem.cart.product_id,
                    "title" to (cartItem.product?.title ?: ""),
                    "coast" to (cartItem.product?.cost ?: 0.0),
                    "count" to cartItem.cart.count
                )
            }

            val response = service.createOrderItems(orderItems, "Bearer $token")

            if (response.isSuccessful) {
                Log.d(tag, "Order items created successfully")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in createOrderItems", e)
            false
        }
    }
}