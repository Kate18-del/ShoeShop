package com.example.shoeshop.data.repository

import Product
import android.util.Log
import com.example.shoeshop.data.RetrofitInstance

class ProductRepository {

    private val service = RetrofitInstance.userManagementService
    private val tag = "ProductRepository"

    suspend fun getProductById(productId: String, token: String): Product? {
        return try {
            Log.d(tag, "Getting product: $productId")
            val filter = "eq.$productId"
            val response = service.getProductById(filter, "Bearer $token")

            if (response.isSuccessful) {
                val products = response.body()
                products?.firstOrNull()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error response: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception in getProductById", e)
            null
        }
    }
}