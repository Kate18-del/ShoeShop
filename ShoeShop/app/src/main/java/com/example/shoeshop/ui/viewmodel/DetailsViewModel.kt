package com.example.shoeshop.ui.viewmodel

import Product
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoeshop.data.AuthManager

import com.example.shoeshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailsState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val currentIndex: Int = 0,
    val isDescriptionExpanded: Boolean = false,
    val error: String? = null
) {
    val currentProduct: Product? get() = products.getOrNull(currentIndex)
}

class DetailsViewModel : ViewModel() {

    private val productRepository = ProductRepository()
    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    private var currentToken: String = ""

    init {
        viewModelScope.launch {
            AuthManager.accessToken.collect { token ->
                if (token != null) {
                    currentToken = token
                    Log.d("DetailsViewModel", "Token received: ${token.take(20)}...")
                } else {
                    Log.d("DetailsViewModel", "No token available")
                }
            }
        }
    }

    fun loadProducts(productId: String) {
        viewModelScope.launch {
            Log.d("DetailsViewModel", "Loading products, productId: $productId")
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Загружаем все товары
                val allProducts = productRepository.getProducts(currentToken) ?: emptyList()
                Log.d("DetailsViewModel", "Products loaded: ${allProducts.size}")

                if (allProducts.isEmpty()) {
                    Log.e("DetailsViewModel", "No products loaded from repository")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Нет товаров для отображения"
                        )
                    }
                    return@launch
                }

                // Находим индекс текущего товара
                val index = allProducts.indexOfFirst { it.id == productId }
                Log.d("DetailsViewModel", "Product index: $index, total products: ${allProducts.size}")

                _state.update {
                    it.copy(
                        isLoading = false,
                        products = allProducts,
                        currentIndex = if (index >= 0) index else 0,
                        isDescriptionExpanded = false
                    )
                }

                Log.d("DetailsViewModel", "State updated, current product: ${_state.value.currentProduct?.title}")

            } catch (e: Exception) {
                Log.e("DetailsViewModel", "Error loading products", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки: ${e.message}"
                    )
                }
            }
        }
    }

    fun setCurrentIndex(index: Int) {
        if (index in 0 until _state.value.products.size) {
            _state.update { it.copy(currentIndex = index) }
            Log.d("DetailsViewModel", "Current index set to: $index")
        }
    }

    fun toggleDescription() {
        _state.update { it.copy(isDescriptionExpanded = !it.isDescriptionExpanded) }
        Log.d("DetailsViewModel", "Description expanded: ${_state.value.isDescriptionExpanded}")
    }

    fun nextProduct() {
        val newIndex = _state.value.currentIndex + 1
        if (newIndex < _state.value.products.size) {
            _state.update { it.copy(currentIndex = newIndex) }
            Log.d("DetailsViewModel", "Next product: $newIndex")
        }
    }

    fun previousProduct() {
        val newIndex = _state.value.currentIndex - 1
        if (newIndex >= 0) {
            _state.update { it.copy(currentIndex = newIndex) }
            Log.d("DetailsViewModel", "Previous product: $newIndex")
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}