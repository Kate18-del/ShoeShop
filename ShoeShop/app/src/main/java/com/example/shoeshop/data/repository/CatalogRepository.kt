package com.example.shoeshop.data.repository

import Category
import Product
import android.util.Log
import com.example.shoeshop.data.RetrofitInstance

class CatalogRepository {

    private val service = RetrofitInstance.userManagementService
    private val tag = "CatalogRepository"

    // Получить все категории
    suspend fun getCategories(token: String): List<Category>? {
        return try {
            Log.d(tag, "Loading categories")
            val response = service.getCategories("Bearer $token")

            if (response.isSuccessful) {
                val categories = response.body()
                Log.d(tag, "Categories loaded: ${categories?.size}")
                categories
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error loading categories: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception loading categories", e)
            null
        }
    }

    // Получить все товары
    suspend fun getProducts(token: String): List<Product>? {
        return try {
            Log.d(tag, "Loading products")
            val response = service.getProducts("Bearer $token")

            if (response.isSuccessful) {
                val products = response.body()
                Log.d(tag, "Products loaded: ${products?.size}")
                products
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error loading products: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception loading products", e)
            null
        }
    }

    // Получить бестселлеры
    suspend fun getBestSellers(token: String): List<Product>? {
        val allProducts = getProducts(token)
        return allProducts?.filter { it.category == "BEST SELLER" }
    }

    // Получить товары по категории
    suspend fun getProductsByCategory(token: String, categoryName: String): List<Product>? {
        val allProducts = getProducts(token)
        return allProducts?.filter { it.category == categoryName }
    }

    // Получить товары по ID категории (если нужно)
    suspend fun getProductsByCategoryId(token: String, categoryId: String): List<Product>? {
        return try {
            Log.d(tag, "Loading products for category: $categoryId")
            val filter = "eq.$categoryId"
            val response = service.getProductsByCategory(filter, "Bearer $token")

            if (response.isSuccessful) {
                val products = response.body()
                Log.d(tag, "Products loaded: ${products?.size}")
                products
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(tag, "Error loading products: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception loading products", e)
            null
        }
    }
}