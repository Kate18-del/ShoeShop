package com.example.shoeshop.data.repository

import Product
import android.util.Log
import com.example.shoeshop.data.RetrofitInstance
import com.example.shoeshop.data.model.Favorite

class FavoriteRepository {

    private val service = RetrofitInstance.userManagementService
    private val tag = "FavoriteRepository"

    // Получить все избранные товары пользователя
    suspend fun getFavorites(userId: String, token: String): List<Favorite>? {
        return try {
            Log.d(tag, "Getting favorites for user: $userId")
            val filter = "eq.$userId"
            val response = service.getFavorites(filter, "Bearer $token")

            if (response.isSuccessful) {
                val favorites = response.body()
                Log.d(tag, "Favorites loaded: ${favorites?.size}")
                favorites
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in getFavorites", e)
            null
        }
    }

    // Добавить товар в избранное
    suspend fun addToFavorite(userId: String, productId: String, token: String): Boolean {
        return try {
            Log.d(tag, "Adding product $productId to favorites for user $userId")

            val favorite = mapOf(
                "user_id" to userId,
                "product_id" to productId
            )

            val response = service.addToFavorite(favorite, "Bearer $token")

            if (response.isSuccessful) {
                Log.d(tag, "Added to favorites successfully")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in addToFavorite", e)
            false
        }
    }

    // Удалить товар из избранного
    suspend fun removeFromFavorite(favoriteId: String, token: String): Boolean {
        return try {
            Log.d(tag, "Removing favorite: $favoriteId")
            val filter = "eq.$favoriteId"
            val response = service.removeFromFavorite(filter, "Bearer $token")

            if (response.isSuccessful) {
                Log.d(tag, "Removed from favorites successfully")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in removeFromFavorite", e)
            false
        }
    }

    // Получить избранные товары с полной информацией о продуктах
    suspend fun getFavoriteProducts(
        userId: String,
        token: String,
        productRepository: ProductRepository
    ): List<Pair<Favorite, Product?>> {
        val favorites = getFavorites(userId, token) ?: return emptyList()

        val result = mutableListOf<Pair<Favorite, Product?>>()

        for (favorite in favorites) {
            val product = productRepository.getProductById(favorite.product_id, token)
            result.add(Pair(favorite, product))
        }

        return result
    }
}