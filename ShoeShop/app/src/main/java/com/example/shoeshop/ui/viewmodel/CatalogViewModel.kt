package com.example.shoeshop.ui.viewmodel

import Category
import Product
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoeshop.data.repository.CatalogRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.map

data class CatalogState(
    val isLoading: Boolean = false,
    val categories: List<Category> = listOf(
        Category("Все", true),
        Category("Outdoor", false),
        Category("Tennis", false),
        Category("Бег", false),
        Category("Баскетбол", false)
    ),
    val allProducts: List<Product> = emptyList(),
    val bestSellers: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategory: String = "Все",
    val error: String? = null
)

class CatalogViewModel : ViewModel() {

    private val repository = CatalogRepository()
    private val _state = MutableStateFlow(CatalogState())
    val state: StateFlow<CatalogState> = _state.asStateFlow()

    private var currentToken: String = ""

    init {
        viewModelScope.launch {
            AuthManager.accessToken.collect { token ->
                if (token != null) {
                    currentToken = token
                    loadCatalog()
                } else {
                    // Для превью загружаем тестовые данные
                    loadMockData()
                }
            }
        }
    }

    private fun loadMockData() {
        val mockProducts = listOf(
            Product(
                id = "1",
                name = "Nike Air Max",
                price = "P752.00",
                originalPrice = "P850.00",
                category = "BEST SELLER",
                imageResId = com.example.shoeshop.R.drawable.nike_zoom_winflo_3_831561_001_mens_running_shoes_11550187236tiyyje6l87_prev_ui_3
            ),
            Product(
                id = "2",
                name = "Nike Air Force 1",
                price = "P820.00",
                originalPrice = "P900.00",
                category = "BEST SELLER",
                imageResId = com.example.shoeshop.R.drawable.nike_zoom_winflo_3_831561_001_mens_running_shoes_11550187236tiyyje6l87_prev_ui_3
            ),
            Product(
                id = "3",
                name = "Adidas Ultraboost",
                price = "P680.00",
                originalPrice = "P750.00",
                category = "NEW",
                imageResId = com.example.shoeshop.R.drawable.nike_zoom_winflo_3_831561_001_mens_running_shoes_11550187236tiyyje6l87_prev_ui_3
            ),
            Product(
                id = "4",
                name = "Puma RS-X",
                price = "P520.00",
                originalPrice = "P600.00",
                category = "TRENDING",
                imageResId = com.example.shoeshop.R.drawable.nike_zoom_winflo_3_831561_001_mens_running_shoes_11550187236tiyyje6l87_prev_ui_3
            )
        )

        _state.update {
            it.copy(
                isLoading = false,
                allProducts = mockProducts,
                bestSellers = mockProducts.filter { it.category == "BEST SELLER" },
                filteredProducts = mockProducts
            )
        }
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Здесь загрузка с сервера
                // val categories = repository.getCategories(currentToken) ?: emptyList()
                // val products = repository.getProducts(currentToken) ?: emptyList()

                // Пока используем мок-данные
                loadMockData()

            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error loading catalog", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки: ${e.message}"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadCatalog()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun selectCategory(categoryName: String) {
        val currentState = _state.value

        // Обновляем isSelected для категорий
        val updatedCategories = currentState.categories.map {
            it.copy(isSelected = it.name == categoryName)
        }

        _state.update {
            it.copy(
                categories = updatedCategories,
                selectedCategory = categoryName
            )
        }

        // Фильтруем товары
        if (categoryName == "Все") {
            _state.update { it.copy(filteredProducts = currentState.allProducts) }
        } else {
            val filtered = currentState.allProducts.filter { it.category == categoryName }
            _state.update { it.copy(filteredProducts = filtered) }
        }
    }
}