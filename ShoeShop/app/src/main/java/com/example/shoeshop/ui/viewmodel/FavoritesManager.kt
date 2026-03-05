package com.example.shoeshop.ui.viewmodel


import Product
import android.util.Log
import com.example.shoeshop.data.model.Favorite

import com.example.shoeshop.data.repository.FavoriteRepository
import com.example.shoeshop.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object FavoritesManager {

    private val favoriteRepository = FavoriteRepository()
    private val productRepository = ProductRepository()

    private val _favorites = MutableStateFlow<List<Pair<Favorite, Product?>>>(emptyList())
    val favorites: StateFlow<List<Pair<Favorite, Product?>>> = _favorites.asStateFlow()

    private val _favoriteProductIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteProductIds: StateFlow<Set<String>> = _favoriteProductIds.asStateFlow()

    private var currentUserId: String = ""
    private var currentToken: String = ""

    fun init(userId: String, token: String) {
        currentUserId = userId
        currentToken = token
        loadFavorites()
    }

    fun loadFavorites() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favorites = favoriteRepository.getFavorites(currentUserId, currentToken) ?: emptyList()

                val favoritesWithProducts = mutableListOf<Pair<Favorite, Product?>>()
                val productIds = mutableSetOf<String>()

                for (favorite in favorites) {
                    productIds.add(favorite.product_id)
                    val product = productRepository.getProductById(favorite.product_id, currentToken)
                    favoritesWithProducts.add(Pair(favorite, product))
                }

                withContext(Dispatchers.Main) {
                    _favorites.value = favoritesWithProducts
                    _favoriteProductIds.value = productIds
                }

                Log.d("FavoritesManager", "Loaded ${favorites.size} favorites")

            } catch (e: Exception) {
                Log.e("FavoritesManager", "Error loading favorites", e)
            }
        }
    }

    fun toggleFavorite(productId: String, onComplete: (Boolean) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            val isFavorite = _favoriteProductIds.value.contains(productId)

            if (isFavorite) {
                // Найти ID избранного
                val favorite = _favorites.value.find { it.first.product_id == productId }
                if (favorite != null) {
                    val success = favoriteRepository.removeFromFavorite(favorite.first.id, currentToken)
                    if (success) {
                        withContext(Dispatchers.Main) {
                            // Обновляем состояние
                            _favorites.value = _favorites.value.filter { it.first.product_id != productId }
                            _favoriteProductIds.value = _favoriteProductIds.value.minus(productId)
                            onComplete(true)
                        }
                    } else {
                        onComplete(false)
                    }
                }
            } else {
                val success = favoriteRepository.addToFavorite(currentUserId, productId, currentToken)
                if (success) {
                    // Перезагружаем весь список
                    loadFavorites()
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
        }
    }

    fun removeFavorite(favoriteId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val success = favoriteRepository.removeFromFavorite(favoriteId, currentToken)
            if (success) {
                withContext(Dispatchers.Main) {
                    // Удаляем из текущего списка
                    _favorites.value = _favorites.value.filter { it.first.id != favoriteId }
                    _favoriteProductIds.value = _favorites.value.map { it.first.product_id }.toSet()
                }
            }
        }
    }

    fun isFavorite(productId: String): Boolean {
        return _favoriteProductIds.value.contains(productId)
    }

    fun clear() {
        _favorites.value = emptyList()
        _favoriteProductIds.value = emptySet()
    }
}