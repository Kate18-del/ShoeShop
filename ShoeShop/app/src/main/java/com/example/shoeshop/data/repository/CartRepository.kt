package com.example.shoeshop.data.repository

import android.util.Log
import com.example.shoeshop.data.RetrofitInstance
import com.example.shoeshop.data.model.Cart
import com.example.shoeshop.data.model.AddToCartRequest
import com.example.shoeshop.data.model.UpdateCartRequest

class CartRepository {

    private val service = RetrofitInstance.userManagementService
    private val tag = "CartRepository"

    // Получить корзину пользователя
    suspend fun getCart(userId: String, token: String): List<Cart>? {
        return try {
            Log.d(tag, "Getting cart for user: $userId")
            val filter = "eq.$userId"
            val response = service.getCart(filter, "Bearer $token")

            if (response.isSuccessful) {
                val cart = response.body()
                Log.d(tag, "Cart loaded: ${cart?.size} items")
                cart
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in getCart", e)
            null
        }
    }

    // Добавить товар в корзину
    suspend fun addToCart(userId: String, productId: String, token: String): Cart? {
        return try {
            Log.d(tag, "Adding product $productId to cart for user $userId")

            // Используем импортированный data class
            val request = AddToCartRequest(
                user_id = userId,
                product_id = productId,
                count = 1
            )

            val response = service.addToCart(request, "Bearer $token")

            if (response.isSuccessful) {
                val created = response.body()
                Log.d(tag, "Added to cart successfully")
                created?.firstOrNull()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in addToCart", e)
            null
        }
    }

    // Обновить количество товара
    suspend fun updateCartItemCount(cartId: String, newCount: Int, token: String): Boolean {
        return try {
            Log.d(tag, "Updating cart item $cartId to count: $newCount")
            val filter = "eq.$cartId"

            // Используем импортированный data class
            val request = UpdateCartRequest(count = newCount)

            val response = service.updateCartItem(filter, request, "Bearer $token")

            if (response.isSuccessful) {
                Log.d(tag, "Cart item updated successfully")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in updateCartItemCount", e)
            false
        }
    }

    // Удалить товар из корзины
    suspend fun removeFromCart(cartId: String, token: String): Boolean {
        return try {
            Log.d(tag, "Removing cart item: $cartId")
            val filter = "eq.$cartId"
            val response = service.removeFromCart(filter, "Bearer $token")

            if (response.isSuccessful) {
                Log.d(tag, "Removed from cart successfully")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in removeFromCart", e)
            false
        }
    }
}