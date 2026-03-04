package com.example.shoeshop.ui.viewmodel

import Product
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

                for (favorite in favorites) {
                    val product = productRepository.getProductById(favorite.product_id, currentToken)
                    favoritesWithProducts.add(Pair(favorite, product))
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        favorites = favoritesWithProducts
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

    fun addToFavorite(productId: String) {
        viewModelScope.launch {
            val success = favoriteRepository.addToFavorite(currentUserId, productId, currentToken)
            if (success) {
                loadFavorites() // Перезагружаем список
            } else {
                _state.update { it.copy(error = "Ошибка добавления в избранное") }
            }
        }
    }

    fun removeFromFavorite(favoriteId: String) {
        viewModelScope.launch {
            val success = favoriteRepository.removeFromFavorite(favoriteId, currentToken)
            if (success) {
                // Удаляем из текущего списка
                val updatedFavorites = _state.value.favorites.filter { it.first.id != favoriteId }
                _state.update { it.copy(favorites = updatedFavorites) }
            } else {
                _state.update { it.copy(error = "Ошибка удаления из избранного") }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}