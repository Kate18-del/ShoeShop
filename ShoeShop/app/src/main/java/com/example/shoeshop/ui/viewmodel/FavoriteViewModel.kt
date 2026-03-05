package com.example.shoeshop.ui.viewmodel

import Product
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoeshop.data.AuthManager
import com.example.shoeshop.data.model.Favorite
import com.example.shoeshop.data.repository.FavoriteRepository
import com.example.shoeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoriteState(
    val isLoading: Boolean = false,
    val favorites: List<Pair<Favorite, Product?>> = emptyList(),
    val favoriteProductIds: Set<String> = emptySet(),
    val error: String? = null
)

class FavoriteViewModel : ViewModel() {

    private val favoriteRepository = FavoriteRepository()
    private val productRepository = ProductRepository()
    private val _state = MutableStateFlow(FavoriteState())
    val state: StateFlow<FavoriteState> = _state.asStateFlow()

    private var currentUserId: String = ""
    private var currentToken: String = ""

    init {
        viewModelScope.launch {
            AuthManager.userId.collect { userId ->
                if (userId != null) {
                    currentUserId = userId
                    loadFavorites()
                }
            }

            AuthManager.accessToken.collect { token ->
                if (token != null) {
                    currentToken = token
                }
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val favorites = favoriteRepository.getFavorites(currentUserId, currentToken) ?: emptyList()

                val favoritesWithProducts = mutableListOf<Pair<Favorite, Product?>>()
                val productIds = mutableSetOf<String>()

                for (favorite in favorites) {
                    productIds.add(favorite.product_id)
                    val product = productRepository.getProductById(favorite.product_id, currentToken)
                    favoritesWithProducts.add(Pair(favorite, product))
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        favorites = favoritesWithProducts,
                        favoriteProductIds = productIds
                    )
                }

            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error loading favorites", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleFavorite(productId: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val isFavorite = _state.value.favoriteProductIds.contains(productId)

            if (isFavorite) {
                // Найти ID избранного
                val favorite = _state.value.favorites.find { it.first.product_id == productId }
                if (favorite != null) {
                    val success = favoriteRepository.removeFromFavorite(favorite.first.id, currentToken)
                    if (success) {
                        // Обновляем состояние
                        val updatedFavorites = _state.value.favorites.filter { it.first.product_id != productId }
                        val updatedIds = _state.value.favoriteProductIds.minus(productId)
                        _state.update {
                            it.copy(
                                favorites = updatedFavorites,
                                favoriteProductIds = updatedIds
                            )
                        }
                        onComplete(true)
                    } else {
                        _state.update { it.copy(error = "Ошибка удаления из избранного") }
                        onComplete(false)
                    }
                }
            } else {
                val success = favoriteRepository.addToFavorite(currentUserId, productId, currentToken)
                if (success) {
                    // Перезагружаем весь список, чтобы получить новый favorite с ID
                    loadFavorites()
                    onComplete(true)
                } else {
                    _state.update { it.copy(error = "Ошибка добавления в избранное") }
                    onComplete(false)
                }
            }
        }
    }

    // Добавляем метод removeFavorite для FavoriteScreen
    fun removeFavorite(favoriteId: String) {
        viewModelScope.launch {
            val success = favoriteRepository.removeFromFavorite(favoriteId, currentToken)
            if (success) {
                // Удаляем из текущего списка
                val updatedFavorites = _state.value.favorites.filter { it.first.id != favoriteId }
                val updatedIds = _state.value.favoriteProductIds.filter {
                    val favorite = _state.value.favorites.find { it.first.id == favoriteId }
                    it != favorite?.first?.product_id
                }.toSet()

                _state.update {
                    it.copy(
                        favorites = updatedFavorites,
                        favoriteProductIds = updatedIds
                    )
                }
            } else {
                _state.update { it.copy(error = "Ошибка удаления из избранного") }
            }
        }
    }

    fun isFavorite(productId: String): Boolean {
        return _state.value.favoriteProductIds.contains(productId)
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}